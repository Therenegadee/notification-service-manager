package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Integer> {
}
