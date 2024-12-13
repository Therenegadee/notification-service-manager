package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEventSendHistoryError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationEventSendHistoryErrorRepository extends JpaRepository<NotificationEventSendHistoryError, Long> {

}
