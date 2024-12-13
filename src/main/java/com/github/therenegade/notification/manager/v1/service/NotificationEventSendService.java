package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Subscription;
import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.operations.sendnotification.SendTelegramNotificationOperation;
import com.github.therenegade.notification.manager.operations.sendnotification.requests.SendTelegramNotificationRequest;
import com.github.therenegade.notification.manager.operations.sendnotification.results.SendNotificationInKafkaResult;
import com.github.therenegade.notification.manager.repository.SubscriptionRepository;
import com.github.therenegade.notification.manager.service.PlaceholderResolver;
import com.github.therenegade.notification.manager.util.TextPlaceholderReplacingUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationEventSendService {

    private final SendTelegramNotificationOperation sendTelegramNotificationOperation;
    private final SubscriptionRepository subscriptionRepository;
    private final PlaceholderResolver placeholderResolver;
    private final ExecutorService telegramSendNotificationsExecutor;

    public NotificationEventSendService(SendTelegramNotificationOperation sendTelegramNotificationOperation,
                                        SubscriptionRepository subscriptionRepository,
                                        PlaceholderResolver placeholderResolver,
                                        @Qualifier("telegramSendNotificationsExecutor") ExecutorService telegramSendNotificationsExecutor) {
        this.sendTelegramNotificationOperation = sendTelegramNotificationOperation;
        this.subscriptionRepository = subscriptionRepository;
        this.placeholderResolver = placeholderResolver;
        this.telegramSendNotificationsExecutor = telegramSendNotificationsExecutor;
    }

    public void sendNotification(@NotNull NotificationEvent notificationEvent) {
        log.info("Starting of processing notification event with id = {}.", notificationEvent.getId());
        if (notificationEvent.getMessages().isEmpty()) {
            log.warn("Notification event with id = {} has no any message to sent!", notificationEvent.getId());
            return;
        }
        Map<NotificationChannelType, NotificationMessage> notificationChannelTypeMessages = notificationEvent.getMessages()
                .stream()
                .collect(Collectors.toMap(message -> message.getNotificationChannel().getAlias(), message -> message));
        Set<NotificationChannelType> notificationChannelTypes = notificationChannelTypeMessages.keySet();
        Map<NotificationChannelType, List<Subscription>> subscriptions =
                subscriptionRepository.findSubscriptionsByNotificationEventType(notificationEvent.getEventType().getId())
                        .stream()
                        .collect(Collectors.groupingBy(subscription -> subscription.getNotificationChannel().getAlias()));
        notificationChannelTypes.forEach(notificationChannelType ->
                CompletableFuture.runAsync(() -> sendTelegramNotification(
                                notificationEvent,
                                notificationChannelTypeMessages.get(notificationChannelType),
                                subscriptions.get(notificationChannelType)
                        ),
                        telegramSendNotificationsExecutor)
        );
    }

    private void sendTelegramNotification(NotificationEvent notificationEvent,
                                          NotificationMessage notificationMessage,
                                          List<Subscription> subscriptions) {
        if (subscriptions.isEmpty()) {
            log.warn("There are no any subscriptions was found for event with id = {}.", notificationEvent.getId());
            return;
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
        log.info("Start sending the prepared messages to users with ids: {}.", Arrays.toString(userIds.toArray()));
        subscriptions.forEach(subscription -> CompletableFuture.runAsync(() -> {
                    String contactValue = subscription.getContactValue();
                    String preparedMessage = messagesByUserId.get(subscription.getUserId());
                    log.info("Sending the prepared message to user with id = {} (contact value = {}). Prepared message: \"{}\"",
                            subscription.getUserId(), contactValue, preparedMessage);
                    var request = new SendTelegramNotificationRequest(contactValue, preparedMessage);
                    sendTelegramNotificationOperation.sendNotification(request);
                }, telegramSendNotificationsExecutor)
        );
    }
}
