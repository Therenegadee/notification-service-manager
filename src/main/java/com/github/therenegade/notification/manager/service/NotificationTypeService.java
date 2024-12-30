package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.entity.NotificationType;

import java.util.List;

public interface NotificationTypeService {

    NotificationType findById(Integer id);

    List<NotificationType> findAll();
}
