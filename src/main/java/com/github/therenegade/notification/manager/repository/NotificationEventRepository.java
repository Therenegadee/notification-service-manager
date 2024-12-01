package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Integer> {

    @Query("""
            SELECT e FROM NotificationEvent e
            WHERE e.executionType = 'TIMESTAMP'
            AND e.isActive = 'true'
            """
    )
    List<NotificationEvent> findActiveTimestampNotificationEvents();

    @Query("""
            SELECT e FROM NotificationEvent e
            WHERE e.executionType = 'CRON'
            AND e.isActive = 'true'
            """
    )
    List<NotificationEvent> findActiveCronNotificationEvents();
}
