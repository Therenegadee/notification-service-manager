package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.requests.CreateTimestampNotificationEventRequest;
import com.github.therenegade.notification.manager.entity.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> findAll();

    Notification createTimestampNotificationEvent(CreateTimestampNotificationEventRequest request);
}
