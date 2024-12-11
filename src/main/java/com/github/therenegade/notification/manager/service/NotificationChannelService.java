package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.entity.NotificationChannel;
import com.github.therenegade.notification.manager.entity.NotificationEventType;

import java.util.List;

public interface NotificationChannelService {

    List<NotificationChannel> findAll();
}
