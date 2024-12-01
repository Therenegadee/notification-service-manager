package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.service.ScheduledNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CronNotificationService implements ScheduledNotificationService {

    @Override
    public void sendScheduledNotifications() {

    }
}
