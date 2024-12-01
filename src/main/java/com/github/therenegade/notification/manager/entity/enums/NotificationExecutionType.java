package com.github.therenegade.notification.manager.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationExecutionType {
    CRON("Scheduled at a regular interval"),
    TIMESTAMP("Scheduled for a specific date and time");

    private final String name;

}
