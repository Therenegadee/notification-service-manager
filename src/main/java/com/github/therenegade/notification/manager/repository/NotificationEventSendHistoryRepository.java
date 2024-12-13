package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventSendHistoryRepository extends JpaRepository<NotificationEventSendHistory, Long> {

    @EntityGraph(attributePaths = {"notificationEvent.eventType"})
    @Query("""
            SELECT h FROM NotificationEventSendHistory h
            WHERE h.notificationEvent.id IN (:eventsIds)
            AND h.notificationEvent.executionType = 'TIMESTAMP'
            AND h.notificationEvent.isActive = true
            """)
    List<NotificationEventSendHistory> findAllTimestampScheduledEvents(List<Integer> eventsIds);

    @Query("""
            SELECT h FROM NotificationEventSendHistory h
            WHERE h.notificationEvent.id IN (:eventsIds)
            AND h.notificationEvent.executionType = 'CRON'
            AND h.notificationEvent.isActive = true
            """)
    List<NotificationEventSendHistory> findAllCronScheduledEvents(List<Integer> eventsIds);
}
