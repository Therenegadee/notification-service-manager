package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationSendError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSendErrorRepository extends JpaRepository<NotificationSendError, Long> {

}
