package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventTypeRepository extends JpaRepository<NotificationEventType, Integer> {
}
