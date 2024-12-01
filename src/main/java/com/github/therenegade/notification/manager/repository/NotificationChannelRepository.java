package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, Integer> {
}
