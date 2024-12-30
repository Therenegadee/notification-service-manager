package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {
}
