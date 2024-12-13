package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistoryError;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.entity.Subscription;
import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.entity.enums.NotificationSendStage;
import com.github.therenegade.notification.manager.exceptions.sender.NoMessagesToSentExceptions;
import com.github.therenegade.notification.manager.exceptions.sender.NoSubscriptionsForEventException;
import com.github.therenegade.notification.manager.exceptions.sender.NotificationNotSentInKafkaException;
import com.github.therenegade.notification.manager.exceptions.sender.NotificationSendingErrorFinishedException;
import com.github.therenegade.notification.manager.v1.sender.SendTelegramNotificationInKafkaService;
import com.github.therenegade.notification.manager.v1.sender.requests.SendNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.requests.SendTelegramNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.results.SendNotificationInKafkaResult;
import com.github.therenegade.notification.manager.repository.NotificationEventSendHistoryRepository;
import com.github.therenegade.notification.manager.repository.SubscriptionRepository;
import com.github.therenegade.notification.manager.util.TextPlaceholderReplacingUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationEventSendService {

    private final SendTelegramNotificationInKafkaService sendTelegramNotificationOperation;
    private final SubscriptionRepository subscriptionRepository;
    private final PlaceholderResolver placeholderResolver;
    private final NotificationEventSendHistoryRepository notificationEventSendHistoryRepository;
    private final ExecutorService telegramSendNotificationsExecutor;

    public NotificationEventSendService(SendTelegramNotificationInKafkaService sendTelegramNotificationOperation,
                                        SubscriptionRepository subscriptionRepository,
                                        PlaceholderResolver placeholderResolver,
                                        NotificationEventSendHistoryRepository notificationEventSendHistoryRepository,
                                        @Qualifier("telegramSendNotificationsExecutor") ExecutorService telegramSendNotificationsExecutor) {
        this.sendTelegramNotificationOperation = sendTelegramNotificationOperation;
        this.subscriptionRepository = subscriptionRepository;
        this.placeholderResolver = placeholderResolver;
        this.telegramSendNotificationsExecutor = telegramSendNotificationsExecutor;
        this.notificationEventSendHistoryRepository = notificationEventSendHistoryRepository;
    }

    public List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> sendNotification(
            @NotNull NotificationEvent notificationEvent
    ) {
        NotificationEventSendHistory sendHistory = NotificationEventSendHistory.builder()
                .notificationEvent(notificationEvent)
                .startTime(OffsetDateTime.now())
                .stage(NotificationSendStage.IN_PROCESS)
                .sendingErrors(new HashSet<>())
                .build();
        try {
            log.info("Starting of processing notification event with id = {}.", notificationEvent.getId());

            notificationEventSendHistoryRepository.save(sendHistory);

            if (notificationEvent.getMessages().isEmpty()) {
                log.warn("Notification event with id = {} has no any message to sent!", notificationEvent.getId());
                String errorMessage = String.format("Notification event with id = %s has no message to sent!", notificationEvent.getId());
                log.error(errorMessage);
                throw new NoMessagesToSentExceptions(errorMessage);
            }

            Map<NotificationChannelType, NotificationMessage> notificationChannelTypeMessages = notificationEvent.getMessages()
                    .stream()
                    .collect(Collectors.toMap(message -> message.getNotificationChannel().getAlias(), message -> message));

            Set<NotificationChannelType> notificationChannelTypes = notificationChannelTypeMessages.keySet();

            Map<NotificationChannelType, List<Subscription>> subscriptions =
                    subscriptionRepository.findSubscriptionsByNotificationEventType(notificationEvent.getEventType().getId())
                            .stream()
                            .collect(Collectors.groupingBy(subscription -> subscription.getNotificationChannel().getAlias()));

            Map<NotificationChannelType, List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>>> sentMessageResults = new HashMap<>();
            for (NotificationChannelType notificationChannelType : notificationChannelTypes) {
                switch (notificationChannelType) {
                    case TELEGRAM ->
                            sentMessageResults.computeIfAbsent(notificationChannelType, map -> new ArrayList<>())
                                    .addAll(sendTelegramNotifications(notificationEvent,
                                            notificationChannelTypeMessages.get(notificationChannelType),
                                            subscriptions.get(notificationChannelType),
                                            sendHistory)
                                    );
                }
            }

            List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> result = sentMessageResults.values()
                    .stream()
                    .flatMap(List::stream)
                    .toList();

            sendHistory.setStage(sendHistory.getSendingErrors().isEmpty()
                    ? NotificationSendStage.FINISHED_SUCCESSFULLY
                    : NotificationSendStage.FINISHED_PARTIALLY);
            sendHistory.setFinishTime(OffsetDateTime.now());
            notificationEventSendHistoryRepository.save(sendHistory);

            return result;
        } catch (Exception exception) {
            String errorMessage = String.format("Unexpected error was caught during sending the notification messages of" +
                    "\snotification event with id = %s. Original error message: %s.", notificationEvent.getId(), exception.getMessage());
            log.error("{}\nStackTrace: {}", errorMessage, ExceptionUtils.getStackTrace(exception));

            var sendingError = buildNotificationEventSendHistoryError(exception, errorMessage);
            sendHistory.addSendingError(sendingError);
            sendHistory.setStage(NotificationSendStage.ERROR_FINISHED);
            notificationEventSendHistoryRepository.save(sendHistory);

            throw new NotificationSendingErrorFinishedException(errorMessage, exception);
        }
    }

    /**
     * Operation of sending the notifications related to {@link NotificationChannelType#TELEGRAM} channel.
     *
     * @param notificationEvent   the notification event.
     * @param notificationMessage the message of notification event related to this {@link NotificationChannelType#TELEGRAM} channel.
     * @param subscriptions       the information about recipients' subscribed to this event and this {@link NotificationChannelType}.
     * @param sendHistory         {@link NotificationEventSendHistory} to save the error in case it'll occur during sending notification.
     * @return list of successfully sent notifications in Kafka.
     */
    private List<SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest>> sendTelegramNotifications(
            NotificationEvent notificationEvent,
            NotificationMessage notificationMessage,
            List<Subscription> subscriptions,
            NotificationEventSendHistory sendHistory
    ) {
        if (subscriptions.isEmpty()) {
            String errorMessage = String.format("There are no any subscriptions was found for event with id = %s.",
                    notificationEvent.getId());
            log.error(errorMessage);
            var sendingError = buildNotificationEventSendHistoryError(new NoSubscriptionsForEventException(errorMessage), errorMessage);
            sendHistory.addSendingError(sendingError);
            return Collections.emptyList();
        }

        List<Integer> userIds = subscriptions.stream()
                .map(Subscription::getUserId)
                .toList();

        Map<Integer, String> messagesByRecipientUserId = getPreparedMessagesByRecipientsIds(notificationMessage, userIds, notificationEvent);

        List<SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest>> sendingResults = new ArrayList<>();
        log.info("Start sending the prepared messages to users with ids: {}.", Arrays.toString(userIds.toArray()));
        for (Subscription subscription : subscriptions) {
            try {
                String preparedMessage = messagesByRecipientUserId.get(subscription.getUserId());
                var sendingResult = sendNotificationToTelegramSubscriber(subscription, preparedMessage, sendHistory, notificationEvent.getId());

                if (Objects.nonNull(sendingResult)) {
                    sendingResults.add(sendingResult);
                }

            } catch (Exception exception) {
                String errorMessage = String.format("The error occurred while sending the message of event with id = %s in %s channel" +
                                "\sto recipient with userId = %s. Details: %s.", notificationEvent.getId(), NotificationChannelType.TELEGRAM,
                        subscription.getUserId(), exception.getMessage());
                log.error("{}\nStackTrace: {}", errorMessage, ExceptionUtils.getStackTrace(exception));

                var sendingError = buildNotificationEventSendHistoryError(
                        new NotificationNotSentInKafkaException(errorMessage, exception), errorMessage
                );
                sendHistory.addSendingError(sendingError);
            }
        }
        return sendingResults
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Operation of sending the prepared message to separate recipient in {@link NotificationChannelType#TELEGRAM} channel.
     *
     * @param subscription        information about recipient's subscription which contains its contact value.
     * @param preparedMessage     the prepared message to send.
     * @param sendHistory         {@link NotificationEventSendHistory} to save the error in case it'll occur during sending notification.
     * @param notificationEventId identifier of {@link NotificationEvent}.
     * @return the result of sending the notification in Kafka or {@code null} in case of sending error.
     */
    private SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest> sendNotificationToTelegramSubscriber(
            Subscription subscription,
            String preparedMessage,
            NotificationEventSendHistory sendHistory,
            Integer notificationEventId
    ) {
        String contactValue = subscription.getContactValue();
        log.info("Sending the prepared message of event with id {} to user with id = {} (contact value = {}). Prepared message: \"{}\"",
                notificationEventId, subscription.getUserId(), contactValue, preparedMessage);

        var sendNotificationRequest = new SendTelegramNotificationInKafkaRequest(contactValue, preparedMessage);
        var sendNotificationResult = sendTelegramNotificationOperation.sendNotification(sendNotificationRequest);

        if (sendNotificationResult.isNotificationSent()) {
            log.info("The message of event with id = {} was successfully sent in {} channel to recipient with contact value \"{}\".",
                    notificationEventId,
                    NotificationChannelType.TELEGRAM.getName(),
                    sendNotificationResult.getSendResult().getProducerRecord().value().getRecipientContactValue());
            return sendNotificationResult;
        } else {
            String errorMessage = String.format("""
                            Notification of event with id %s wasn't sent in Kafka topic of channel %s.
                            Error message: "%s";
                            Original exception class: %s;
                            StackTrace: %s
                            """, notificationEventId, NotificationChannelType.TELEGRAM.getName(),
                    sendNotificationResult.getErrorMessage(), sendNotificationResult.getExceptionOccurred().getClass(),
                    ExceptionUtils.getStackTrace(sendNotificationResult.getExceptionOccurred()));
            log.error(errorMessage);

            var sendingError = buildNotificationEventSendHistoryError(
                    new NotificationNotSentInKafkaException(sendNotificationResult.getErrorMessage()), errorMessage
            );
            sendHistory.addSendingError(sendingError);

            return null;
        }
    }

    /**
     * Creating the prepared messages for recipients.
     * <p>
     * In case the propagated {@link NotificationMessage} doesn't have any {@link Placeholder},
     * then the templated message from this {@link NotificationMessage} will be used for all the users.
     *
     * @param notificationMessage notification message with information about {@link Placeholder} and {@link NotificationChannelType}.
     * @param userIds             recipients identifiers.
     * @param notificationEvent   the event {@link NotificationMessage} belongs to.
     * @return prepared messages by recipients ids.
     */
    private Map<Integer, String> getPreparedMessagesByRecipientsIds(NotificationMessage notificationMessage,
                                                                    List<Integer> userIds,
                                                                    NotificationEvent notificationEvent) {
        String originalMessage = notificationMessage.getMessage();
        Map<Integer, String> messagesByUserId = new HashMap<>();

        if (!notificationMessage.getPlaceholders().isEmpty()) {
            log.debug("{} placeholders was found for notification event with id = {} for channel type {}.",
                    notificationMessage.getPlaceholders().size(), notificationEvent.getId(), notificationMessage.getNotificationChannel().getAlias());

            List<ResolvedPlaceholdersInformation> resolvedPlaceholdersInformation =
                    placeholderResolver.resolvePlaceholders(notificationMessage.getPlaceholders(), userIds);
            resolvedPlaceholdersInformation.forEach(info ->
                    messagesByUserId.put(
                            info.getRecipientId(),
                            TextPlaceholderReplacingUtil.replaceAllPlaceholdersInText(originalMessage, info.getResolvedPlaceholderValues())
                    )
            );

        } else {
            log.debug("No placeholders were found for notification event with id = {} (channel type is \"{}\").",
                    notificationEvent.getId(), notificationMessage.getNotificationChannel().getAlias());
            userIds.forEach(userId -> messagesByUserId.put(userId, originalMessage));
        }
        return messagesByUserId;
    }

    private NotificationEventSendHistoryError buildNotificationEventSendHistoryError(@NotNull Exception exception,
                                                                                     @NotNull String message) {
        String details = Objects.nonNull(exception.getCause())
                ? exception.getCause().getMessage()
                : exception.getMessage();
        return NotificationEventSendHistoryError.builder()
                .message(message)
                .exceptionName(exception.getClass().getName())
                .details(details)
                .build();
    }
}
