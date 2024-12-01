package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import com.github.therenegade.notification.manager.repository.NotificationEventRepository;
import com.github.therenegade.notification.manager.repository.NotificationEventSendHistoryRepository;
import com.github.therenegade.notification.manager.service.ScheduledNotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TimestampNotificationService implements ScheduledNotificationService {

    private final NotificationEventRepository notificationEventRepository;
    private final NotificationEventSendHistoryRepository notificationEventSendHistoryRepository;
    private final NotificationEventSendService notificationEventSendService;
    private final ScheduledExecutorService scheduledExecutorService;

    @Value("${scheduler.notifications.timestamp.scheduledSendTimeMs}")
    private int scheduledSendTimeMs;

    public TimestampNotificationService(NotificationEventRepository notificationEventRepository,
                                        NotificationEventSendHistoryRepository notificationEventSendHistoryRepository,
                                        NotificationEventSendService notificationEventSendService,
                                        @Qualifier("timestampNotificationsScheduledExecutor") ScheduledExecutorService scheduledExecutorService) {
        this.notificationEventRepository = notificationEventRepository;
        this.notificationEventSendHistoryRepository = notificationEventSendHistoryRepository;
        this.notificationEventSendService = notificationEventSendService;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @EventListener(ApplicationStartedEvent.class)
    @Override
    public void sendScheduledNotifications() {
        scheduledExecutorService.scheduleAtFixedRate(this::sendTimestampNotifications, 0, scheduledSendTimeMs, TimeUnit.MILLISECONDS);
    }

    private void sendTimestampNotifications() {
        List<NotificationEvent> activeTimestampNotificationEvents =
                notificationEventRepository.findActiveTimestampNotificationEvents();
        List<NotificationEvent> notificationEventsToSend =
                notificationEventSendHistoryRepository.findAllTimestampScheduledEventsNotStarted(activeTimestampNotificationEvents.stream()
                                .map(NotificationEvent::getId)
                                .toList())
                        .stream()
                        .map(NotificationEventSendHistory::getNotificationEvent)
                        .toList();
        notificationEventsToSend.forEach(event -> {
            if (isNotificationNeedsToBeSentNow(event.getExecuteTimestamp())) {
                CompletableFuture.runAsync(() -> notificationEventSendService.sendNotification(event), scheduledExecutorService);
            }
        });
    }


    private boolean isNotificationNeedsToBeSentNow(OffsetDateTime notificationExecuteTimestamp) {
        OffsetDateTime now = OffsetDateTime.now();
        return notificationExecuteTimestamp.isBefore(now) || notificationExecuteTimestamp.isEqual(now);
    }
}
