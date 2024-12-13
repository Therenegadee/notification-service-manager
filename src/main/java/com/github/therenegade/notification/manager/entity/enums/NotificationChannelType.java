package com.github.therenegade.notification.manager.entity.enums;

import lombok.Getter;

@Getter
public enum NotificationChannelType {
    TELEGRAM("Telegram"),
    EMAIL("Email");
    // other channels

    private final String name;

    NotificationChannelType(String name) {
        this.name = name;
    }
}
