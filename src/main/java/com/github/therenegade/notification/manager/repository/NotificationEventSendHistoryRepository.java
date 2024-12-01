package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
import com.github.therenegade.notification.manager.entity.enums.NotificationEventTypeEnum;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventSendHistoryRepository extends JpaRepository<NotificationEventSendHistory, Long> {

    @Query("""
            SELECT h FROM NotificationEventSendHistory h
            WHERE h.notificationEvent.id IN (:eventsIds)
            AND h.notificationEvent.eventType = 'TIMESTAMP'
            AND h.notificationEvent.isActive = 'true'
            AND h.stage = 'NOT_STARTED'
            """)
    List<NotificationEventSendHistory> findAllTimestampScheduledEventsNotStarted(List<Integer> eventsIds);

    @Query("""
            SELECT h FROM NotificationEventSendHistory h
            WHERE h.notificationEvent.eventType = 'CRON'
            AND h.notificationEvent.isActive = 'true'
            AND h.stage = 'NOT_STARTED'
            """)
    List<NotificationEventSendHistory> findAllCronScheduledEventsNotStarted();
}
