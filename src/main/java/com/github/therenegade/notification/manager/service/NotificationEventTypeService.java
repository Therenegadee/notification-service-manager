package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.entity.NotificationEventType;

import java.util.List;

public interface NotificationEventTypeService {

    NotificationEventType findById(Integer id);

    List<NotificationEventType> findAll();
}
