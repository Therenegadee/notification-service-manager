package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationSendQueue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationSendQueueRepository extends JpaRepository<NotificationSendQueue, Long> {

    @EntityGraph(attributePaths = {"notification.eventType"})
    @Query("""
            SELECT h FROM NotificationEventSendHistory h
            WHERE h.notification.id IN (:eventsIds)
            AND h.notification.executionType = 'TIMESTAMP'
            AND h.notification.isActive = true
            """)
    List<NotificationSendQueue> findAllTimestampScheduledEvents(List<Integer> eventsIds);

    @Query("""
            SELECT h FROM NotificationEventSendHistory h
            WHERE h.notification.id IN (:eventsIds)
            AND h.notification.executionType = 'CRON'
            AND h.notification.isActive = true
            """)
    List<NotificationSendQueue> findAllCronScheduledEvents(List<Integer> eventsIds);
}
