package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.requests.CreateTimestampNotificationEventRequest;
import com.github.therenegade.notification.manager.entity.NotificationEvent;

import java.util.List;

public interface NotificationEventService {

    List<NotificationEvent> findAll();

    NotificationEvent createTimestampNotificationEvent(CreateTimestampNotificationEventRequest request);
}
