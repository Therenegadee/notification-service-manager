package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.NotificationSendQueue;
import com.github.therenegade.notification.manager.entity.NotificationSendError;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.entity.Subscription;
import com.github.therenegade.notification.manager.entity.enums.DistributionChannelType;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import com.github.therenegade.notification.manager.entity.enums.NotificationSendStage;
import com.github.therenegade.notification.manager.exceptions.sender.NoMessagesToSentException;
import com.github.therenegade.notification.manager.exceptions.sender.NoSubscriptionsForEventException;
import com.github.therenegade.notification.manager.exceptions.sender.NotificationNotSentInKafkaException;
import com.github.therenegade.notification.manager.exceptions.sender.NotificationSendingErrorFinishedException;
import com.github.therenegade.notification.manager.repository.NotificationRepository;
import com.github.therenegade.notification.manager.repository.NotificationSendQueueRepository;
import com.github.therenegade.notification.manager.repository.SubscriptionRepository;
import com.github.therenegade.notification.manager.util.TextPlaceholderReplacingUtil;
import com.github.therenegade.notification.manager.v1.sender.SendTelegramNotificationInKafkaService;
import com.github.therenegade.notification.manager.v1.sender.requests.SendNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.requests.SendTelegramNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.results.SendNotificationInKafkaResult;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSendService {

    private final SendTelegramNotificationInKafkaService sendTelegramNotificationOperation;
    private final SubscriptionRepository subscriptionRepository;
    private final PlaceholderResolver placeholderResolver;
    private final NotificationSendQueueRepository notificationSendQueueRepository;
    private final NotificationRepository notificationRepository;


    public List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> sendNotification(
            @NotNull Notification notification
    ) {
        NotificationSendQueue sendHistory = NotificationSendQueue.builder()
                .notification(notification)
                .startTime(OffsetDateTime.now())
                .stage(NotificationSendStage.IN_PROCESS)
                .sendingErrors(new HashSet<>())
                .build();
        try {
            log.info("Starting of processing notification event with id = {}.", notification.getId());

            notificationSendQueueRepository.save(sendHistory);

            if (notification.getMessages().isEmpty()) {
                String errorMessage = String.format("Notification event with id = %s has no message to sent!", notification.getId());
                log.error(errorMessage);
                throw new NoMessagesToSentException(errorMessage);
            }

            Map<DistributionChannelType, NotificationMessage> notificationChannelTypeMessages = notification.getMessages()
                    .stream()
                    .collect(Collectors.toMap(message -> message.getDistributionChannel().getAlias(), message -> message));

            Set<DistributionChannelType> distributionChannelTypes = notificationChannelTypeMessages.keySet();

            List<Subscription> eventSubscriptions = subscriptionRepository.findSubscriptionsByNotificationEventType(notification.getEventType().getId());
            if (eventSubscriptions.isEmpty()) {
                String errorMessage = String.format("There are no any subscriptions was found for event with id = %s.",
                        notification.getId());
                log.error(errorMessage);
                throw new NoSubscriptionsForEventException(errorMessage);
            }

            Map<DistributionChannelType, List<Subscription>> subscriptionsByChannel = eventSubscriptions.stream()
                            .collect(Collectors.groupingBy(subscription -> subscription.getDistributionChannel().getAlias()));

            Map<DistributionChannelType, List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>>> sentMessageResults = new HashMap<>();
            for (DistributionChannelType distributionChannelType : distributionChannelTypes) {
                switch (distributionChannelType) {
                    case TELEGRAM ->
                            sentMessageResults.computeIfAbsent(distributionChannelType, map -> new ArrayList<>())
                                    .addAll(sendTelegramNotifications(notification,
                                            notificationChannelTypeMessages.get(distributionChannelType),
                                            subscriptionsByChannel.getOrDefault(distributionChannelType, new ArrayList<>()),
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
            notificationSendQueueRepository.save(sendHistory);

            if (notification.getExecutionType().equals(NotificationExecutionType.TIMESTAMP)) {
                notification.setIsActive(false);
                notificationRepository.save(notification);
            }

            return result;
        } catch (Exception exception) {
            String errorMessage = String.format("Unexpected error was caught during sending the notification messages of" +
                    "\snotification event with id = %s. Original error message: %s.", notification.getId(), exception.getMessage());
            log.error("{}\nStackTrace: {}", errorMessage, ExceptionUtils.getStackTrace(exception));

            var sendingError = buildNotificationEventSendHistoryError(exception, errorMessage);
            sendHistory.addSendingError(sendingError);
            sendHistory.setStage(NotificationSendStage.ERROR_FINISHED);
            notificationSendQueueRepository.save(sendHistory);

            throw new NotificationSendingErrorFinishedException(errorMessage, exception);
        }
    }

    /**
     * Operation of sending the notifications related to {@link DistributionChannelType#TELEGRAM} channel.
     *
     * @param notification   the notification event.
     * @param notificationMessage the message of notification event related to this {@link DistributionChannelType#TELEGRAM} channel.
     * @param subscriptions       the information about recipients' subscribed to this event and this {@link DistributionChannelType}.
     * @param sendHistory         {@link NotificationSendQueue} to save the error in case it'll occur during sending notification.
     * @return list of successfully sent notifications in Kafka.
     */
    private List<SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest>> sendTelegramNotifications(
            Notification notification,
            NotificationMessage notificationMessage,
            List<Subscription> subscriptions,
            NotificationSendQueue sendHistory
    ) {
        if (subscriptions.isEmpty()) {
            String errorMessage = String.format("There are no any subscriptions was found for event with id = %s and %s channel.",
                    notification.getId(), notificationMessage.getDistributionChannel().getAlias());
            log.error(errorMessage);
            var sendingError = buildNotificationEventSendHistoryError(new NoSubscriptionsForEventException(errorMessage), errorMessage);
            sendHistory.addSendingError(sendingError);
            return Collections.emptyList();
        }

        List<Integer> userIds = subscriptions.stream()
                .map(Subscription::getUserId)
                .toList();

        Map<Integer, String> messagesByRecipientUserId = getPreparedMessagesByRecipientsIds(notificationMessage, userIds, notification);

        List<SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest>> sendingResults = new ArrayList<>();
        log.info("Start sending the prepared messages to users with ids: {}.", Arrays.toString(userIds.toArray()));
        for (Subscription subscription : subscriptions) {
            try {
                String preparedMessage = messagesByRecipientUserId.get(subscription.getUserId());
                var sendingResult = sendNotificationToTelegramSubscriber(subscription, preparedMessage, sendHistory, notification.getId());

                if (Objects.nonNull(sendingResult)) {
                    sendingResults.add(sendingResult);
                }

            } catch (Exception exception) {
                String errorMessage = String.format("The error occurred while sending the message of event with id = %s in %s channel" +
                                "\sto recipient with userId = %s. Details: %s.", notification.getId(), DistributionChannelType.TELEGRAM,
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
     * Operation of sending the prepared message to separate recipient in {@link DistributionChannelType#TELEGRAM} channel.
     *
     * @param subscription        information about recipient's subscription which contains its contact value.
     * @param preparedMessage     the prepared message to send.
     * @param sendHistory         {@link NotificationSendQueue} to save the error in case it'll occur during sending notification.
     * @param notificationEventId identifier of {@link Notification}.
     * @return the result of sending the notification in Kafka or {@code null} in case of sending error.
     */
    private SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest> sendNotificationToTelegramSubscriber(
            Subscription subscription,
            String preparedMessage,
            NotificationSendQueue sendHistory,
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
                    DistributionChannelType.TELEGRAM.getName(),
                    sendNotificationResult.getSendResult().getProducerRecord().value().getRecipientContactValue());
            return sendNotificationResult;
        } else {
            String errorMessage = String.format("""
                            Notification of event with id %s wasn't sent in Kafka topic of channel %s.
                            Error message: "%s";
                            Original exception class: %s;
                            StackTrace: %s
                            """, notificationEventId, DistributionChannelType.TELEGRAM.getName(),
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
     * @param notificationMessage notification message with information about {@link Placeholder} and {@link DistributionChannelType}.
     * @param userIds             recipients identifiers.
     * @param notification   the event {@link NotificationMessage} belongs to.
     * @return prepared messages by recipients ids.
     */
    private Map<Integer, String> getPreparedMessagesByRecipientsIds(NotificationMessage notificationMessage,
                                                                    List<Integer> userIds,
                                                                    Notification notification) {
        String originalMessage = notificationMessage.getMessage();
        Map<Integer, String> messagesByUserId = new HashMap<>();

        if (!notificationMessage.getPlaceholders().isEmpty()) {
            log.debug("{} placeholders was found for notification event with id = {} for channel type {}.",
                    notificationMessage.getPlaceholders().size(), notification.getId(), notificationMessage.getDistributionChannel().getAlias());

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
                    notification.getId(), notificationMessage.getDistributionChannel().getAlias());
            userIds.forEach(userId -> messagesByUserId.put(userId, originalMessage));
        }
        return messagesByUserId;
    }

    private NotificationSendError buildNotificationEventSendHistoryError(@NotNull Exception exception,
                                                                         @NotNull String message) {
        String details = Objects.nonNull(exception.getCause())
                ? exception.getCause().getMessage()
                : exception.getMessage();
        return NotificationSendError.builder()
                .message(message)
                .exceptionName(exception.getClass().getName())
                .details(details)
                .build();
    }
}
