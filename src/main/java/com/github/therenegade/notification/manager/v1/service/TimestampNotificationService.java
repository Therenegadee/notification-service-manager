package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import com.github.therenegade.notification.manager.entity.enums.NotificationSendStage;
import com.github.therenegade.notification.manager.repository.NotificationEventRepository;
import com.github.therenegade.notification.manager.repository.NotificationEventSendHistoryRepository;
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

    private final NotificationEventRepository notificationEventRepository;
    private final NotificationMessageRepository notificationMessageRepository;
    private final NotificationEventSendHistoryRepository notificationEventSendHistoryRepository;
    private final NotificationEventSendService notificationEventSendService;
    private final ScheduledExecutorService scheduledExecutorService;

    @Value("${scheduler.notifications.timestamp.scheduledSendTimeMs}")
    private int scheduledSendTimeMs;

    public TimestampNotificationService(NotificationEventRepository notificationEventRepository,
                                        NotificationMessageRepository notificationMessageRepository,
                                        NotificationEventSendHistoryRepository notificationEventSendHistoryRepository,
                                        NotificationEventSendService notificationEventSendService,
                                        @Qualifier("timestampNotificationsScheduledExecutor") ScheduledExecutorService scheduledExecutorService) {
        this.notificationEventRepository = notificationEventRepository;
        this.notificationMessageRepository = notificationMessageRepository;
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
        List<NotificationEvent> activeTimestampNotificationEvents = getActiveTimestampNotificationEvents();

        for (NotificationEvent notificationEvent : activeTimestampNotificationEvents) {
                CompletableFuture.runAsync(() -> notificationEventSendService.sendNotification(notificationEvent), scheduledExecutorService);
        }
    }

    /**
     * Fetching the {@link NotificationEvent} which are an active and have no record in {@link NotificationEventSendHistory}
     * with one of the statuses as follows: {@link NotificationSendStage#FINISHED_PARTIALLY}, {@link NotificationSendStage#FINISHED_SUCCESSFULLY},
     * {@link NotificationSendStage#ERROR_FINISHED}, {@link NotificationSendStage#IN_PROCESS}.
     *
     * @return all active {@link NotificationEvent}.
     */
    private List<NotificationEvent> getActiveTimestampNotificationEvents() {
        OffsetDateTime now = OffsetDateTime.now();
        List<NotificationEvent> activeTimestampNotificationEvents =
                notificationEventRepository.findAll(NotificationEventRepository.buildSpecification(
                        NotificationExecutionType.TIMESTAMP,
                        true,
                        now));

        List<Integer> eventsIds = activeTimestampNotificationEvents.stream()
                .map(NotificationEvent::getId)
                .toList();

        Map<Integer, List<NotificationMessage>> notificationMessagesByEventsIds = notificationMessageRepository.findAll(NotificationMessageRepository.buildSpecification(eventsIds))
                .stream()
                .collect(Collectors.groupingBy(msg -> msg.getNotificationEvent().getId()));

        for (NotificationEvent notificationEvent : activeTimestampNotificationEvents) {
            List<NotificationMessage> notificationEventMessages = notificationMessagesByEventsIds.getOrDefault(notificationEvent.getId(), new ArrayList<>());
            if (notificationEventMessages.isEmpty()) {
                log.warn("No messages were found for event with id = {}.", notificationEvent.getId());
                continue;
            }
            notificationEvent.setMessages(notificationEventMessages);
            notificationEventMessages.forEach(msg -> msg.setNotificationEvent(notificationEvent));
        }

        Map<Integer, NotificationSendStage> notificationSendStagesByEventIds =
                notificationEventSendHistoryRepository.findAllTimestampScheduledEvents(eventsIds)
                        .stream()
                        .collect(Collectors.toMap(history -> history.getNotificationEvent().getId(), NotificationEventSendHistory::getStage));

        return activeTimestampNotificationEvents.stream()
                .filter(event -> !notificationSendStagesByEventIds.containsKey(event.getId())
                        || notificationSendStagesByEventIds.get(event.getId()).equals(NotificationSendStage.NOT_STARTED))
                .toList();
    }
}
