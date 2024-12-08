package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Subscription;
import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.entity.enums.NotificationSendStage;
import com.github.therenegade.notification.manager.exceptions.NoMessagesToSentExceptions;
import com.github.therenegade.notification.manager.exceptions.NoSubscriptionsForEventException;
import com.github.therenegade.notification.manager.exceptions.NotificationSendingErrorFinishedException;
import com.github.therenegade.notification.manager.operations.sendnotification.SendTelegramNotificationInKafkaOperation;
import com.github.therenegade.notification.manager.operations.sendnotification.requests.SendNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.operations.sendnotification.requests.SendTelegramNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.operations.sendnotification.results.SendNotificationInKafkaResult;
import com.github.therenegade.notification.manager.repository.NotificationEventSendHistoryRepository;
import com.github.therenegade.notification.manager.repository.SubscriptionRepository;
import com.github.therenegade.notification.manager.service.PlaceholderResolver;
import com.github.therenegade.notification.manager.util.TextPlaceholderReplacingUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationEventSendService {

    private final SendTelegramNotificationInKafkaOperation sendTelegramNotificationOperation;
    private final SubscriptionRepository subscriptionRepository;
    private final PlaceholderResolver placeholderResolver;
    private final NotificationEventSendHistoryRepository notificationEventSendHistoryRepository;
    private final ExecutorService telegramSendNotificationsExecutor;

    public NotificationEventSendService(SendTelegramNotificationInKafkaOperation sendTelegramNotificationOperation,
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

    public List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> sendNotification(@NotNull NotificationEvent notificationEvent) {
        NotificationEventSendHistory sendHistory = NotificationEventSendHistory.builder()
                .notificationEvent(notificationEvent)
                .stage(NotificationSendStage.IN_PROCESS)
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
                                    .addAll(sendTelegramNotification(notificationEvent,
                                            notificationChannelTypeMessages.get(notificationChannelType),
                                            subscriptions.get(notificationChannelType))
                                    );
                }
            }

            List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> result = sentMessageResults.values()
                    .stream()
                    .flatMap(List::stream)
                    .toList();

            sendHistory.setStage(NotificationSendStage.FINISHED_SUCCESSFULLY);
            sendHistory.setNotificationSentTime(OffsetDateTime.now());
            notificationEventSendHistoryRepository.save(sendHistory);

            return result;
        } catch (Exception exception) {
            sendHistory.setStage(NotificationSendStage.ERROR_FINISHED);
            notificationEventSendHistoryRepository.save(sendHistory);

            String errorMessage = String.format("Unexpected error was catched during sending the notififcation messages of" +
                    "\snotification event with id = %s. Original error message: %s.", notificationEvent.getId(), exception.getMessage());
            log.error("{}\nStackTrace: {}", errorMessage, ExceptionUtils.getStackTrace(exception));
            throw new NotificationSendingErrorFinishedException(errorMessage, exception);
        }
    }

    private List<SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest>> sendTelegramNotification(
            NotificationEvent notificationEvent,
            NotificationMessage notificationMessage,
            List<Subscription> subscriptions
    ) {
        if (subscriptions.isEmpty()) {
            String errorMessage = String.format("There are no any subscriptions was found for event with id = %s.",
                    notificationEvent.getId());
            log.error(errorMessage);
            throw new NoSubscriptionsForEventException(errorMessage); // todo add to errors
        }

        List<Integer> userIds = subscriptions.stream()
                .map(Subscription::getUserId)
                .toList();

        String message = notificationMessage.getMessage();

        Map<Integer, String> messagesByUserId = new HashMap<>();
        if (!notificationMessage.getPlaceholders().isEmpty()) {
            log.debug("{} placeholders was found for notification event with id = {} (channel type is \"{}\").",
                    notificationMessage.getPlaceholders().size(), notificationEvent.getId(), notificationMessage.getNotificationChannel().getAlias());
            List<ResolvedPlaceholdersInformation> resolvedPlaceholdersInformation =
                    placeholderResolver.resolvePlaceholders(notificationMessage.getPlaceholders(), userIds);
            resolvedPlaceholdersInformation.forEach(info ->
                    messagesByUserId.put(info.getRecipientId(), TextPlaceholderReplacingUtil.replaceAllPlaceholdersInText(message, info.getResolvedPlaceholderValues()))
            );
        } else {
            log.debug("No placeholders were found for notification event with id = {} (channel type is \"{}\").",
                    notificationEvent.getId(), notificationMessage.getNotificationChannel().getAlias());
            userIds.forEach(userId -> messagesByUserId.put(userId, message));
        }

        List<SendNotificationInKafkaResult<SendTelegramNotificationInKafkaRequest>> sendingResults = new ArrayList<>();
        log.info("Start sending the prepared messages to users with ids: {}.", Arrays.toString(userIds.toArray()));
        for (Subscription subscription : subscriptions) {
            try {
                String contactValue = subscription.getContactValue();
                String preparedMessage = messagesByUserId.get(subscription.getUserId());
                log.info("Sending the prepared message of event with id {} to user with id = {} (contact value = {}). Prepared message: \"{}\"",
                        notificationEvent.getId(), subscription.getUserId(), contactValue, preparedMessage);

                var request = new SendTelegramNotificationInKafkaRequest(contactValue, preparedMessage);
                var result = sendTelegramNotificationOperation.sendNotification(request);

                if (result.isNotificationSent()) {
                    log.info("The message of event with id = {} was successfully sent in {} channel to recipient with contact value \"{}\".",
                            notificationEvent.getId(),
                            NotificationChannelType.TELEGRAM.getName(),
                            result.getSendResult().getProducerRecord().value().getRecipientContactValue());
                } else {
                    String errorMessage = String.format("""
                                            Notification of event with id %s wasn't sent in Kafka topic of channel %s.
                                            Error message: "%s";
                                            Original exception class: %s;
                                            StackTrace: %s
                                            """, notificationEvent.getId(), NotificationChannelType.TELEGRAM.getName(),
                            result.getErrorMessage(), result.getExceptionOccurred().getClass(),
                            ExceptionUtils.getStackTrace(result.getExceptionOccurred()));
                    log.error(errorMessage);
//                          new NotificationNotSentInKafkaException(errorMessage, result.getExceptionOccurred()); // todo add to errors
                }

                sendingResults.add(result);
            } catch (Exception exception) {
                String errorMessage = String.format("The error occurred while sending the messages of event with id = %s to recipient. Details: %s.",
                        notificationEvent.getId(), exception.getMessage());
                log.error("{}\nStackTrace: {}", errorMessage, ExceptionUtils.getStackTrace(exception));
//                      new NotificationNotSentInKafkaException(errorMessage, exception); // todo add to errors
            }
        }
        return sendingResults;
    }
}
