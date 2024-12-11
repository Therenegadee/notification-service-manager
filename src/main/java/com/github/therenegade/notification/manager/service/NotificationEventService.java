package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.requests.CreateTimestampNotificationEventRequest;
import com.github.therenegade.notification.manager.entity.NotificationEvent;

public interface NotificationEventService {

    NotificationEvent createTimestampNotificationEvent(CreateTimestampNotificationEventRequest request);
}
