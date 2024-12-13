package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.requests.CreateNotificationMessageRequest;
import com.github.therenegade.notification.manager.entity.NotificationMessage;

public interface NotificationMessageService {

    NotificationMessage createNotificationMessage(CreateNotificationMessageRequest request);
}
