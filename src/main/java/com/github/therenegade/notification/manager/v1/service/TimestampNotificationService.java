package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.NotificationSendQueue;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import com.github.therenegade.notification.manager.entity.enums.NotificationSendStage;
import com.github.therenegade.notification.manager.repository.NotificationRepository;
import com.github.therenegade.notification.manager.repository.NotificationSendQueueRepository;
import com.github.therenegade.notification.manager.repository.NotificationMessageRepository;
import com.github.therenegade.notification.manager.service.ScheduledNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TimestampNotificationService implements ScheduledNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMessageRepository notificationMessageRepository;
    private final NotificationSendQueueRepository notificationSendQueueRepository;
    private final NotificationSendService notificationSendService;
    private final ScheduledExecutorService scheduledExecutorService;

    @Value("${scheduler.notifications.timestamp.scheduledSendTimeMs}")
    private int scheduledSendTimeMs;

    public TimestampNotificationService(NotificationRepository notificationRepository,
                                        NotificationMessageRepository notificationMessageRepository,
                                        NotificationSendQueueRepository notificationSendQueueRepository,
                                        NotificationSendService notificationSendService,
                                        @Qualifier("timestampNotificationsScheduledExecutor") ScheduledExecutorService scheduledExecutorService) {
        this.notificationRepository = notificationRepository;
        this.notificationMessageRepository = notificationMessageRepository;
        this.notificationSendQueueRepository = notificationSendQueueRepository;
        this.notificationSendService = notificationSendService;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @EventListener(ApplicationStartedEvent.class)
    @Override
    public void sendScheduledNotifications() {
        scheduledExecutorService.scheduleAtFixedRate(this::sendTimestampNotifications, 0, scheduledSendTimeMs, TimeUnit.MILLISECONDS);
    }

    private void sendTimestampNotifications() {
        List<Notification> activeTimestampNotifications = getActiveTimestampNotificationEvents();

        for (Notification notification : activeTimestampNotifications) {
                CompletableFuture.runAsync(() -> notificationSendService.sendNotification(notification), scheduledExecutorService);
        }
    }

    /**
     * Fetching the {@link Notification} which are an active and have no record in {@link NotificationSendQueue}
     * with one of the statuses as follows: {@link NotificationSendStage#FINISHED_PARTIALLY}, {@link NotificationSendStage#FINISHED_SUCCESSFULLY},
     * {@link NotificationSendStage#ERROR_FINISHED}, {@link NotificationSendStage#IN_PROCESS}.
     *
     * @return all active {@link Notification}.
     */
    private List<Notification> getActiveTimestampNotificationEvents() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Notification> activeTimestampNotifications =
                notificationRepository.findAll(NotificationRepository.buildSpecification(
                        NotificationExecutionType.TIMESTAMP,
                        true,
                        now));

        List<Integer> eventsIds = activeTimestampNotifications.stream()
                .map(Notification::getId)
                .toList();

        Map<Integer, List<NotificationMessage>> notificationMessagesByEventsIds = notificationMessageRepository.findAll(NotificationMessageRepository.buildSpecification(eventsIds))
                .stream()
                .collect(Collectors.groupingBy(msg -> msg.getNotification().getId()));

        for (Notification notification : activeTimestampNotifications) {
            List<NotificationMessage> notificationEventMessages = notificationMessagesByEventsIds.getOrDefault(notification.getId(), new ArrayList<>());
            if (notificationEventMessages.isEmpty()) {
                log.warn("No messages were found for event with id = {}.", notification.getId());
                continue;
            }
            notification.setMessages(notificationEventMessages);
            notificationEventMessages.forEach(msg -> msg.setNotification(notification));
        }

        Map<Integer, NotificationSendStage> notificationSendStagesByEventIds =
                notificationSendQueueRepository.findAllTimestampScheduledEvents(eventsIds)
                        .stream()
                        .collect(Collectors.toMap(history -> history.getNotification().getId(), NotificationSendQueue::getStage));

        return activeTimestampNotifications.stream()
                .filter(event -> !notificationSendStagesByEventIds.containsKey(event.getId())
                        || notificationSendStagesByEventIds.get(event.getId()).equals(NotificationSendStage.NOT_STARTED))
                .toList();
    }
}
