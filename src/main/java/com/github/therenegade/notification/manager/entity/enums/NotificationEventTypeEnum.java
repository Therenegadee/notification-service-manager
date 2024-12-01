package com.github.therenegade.notification.manager.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationEventTypeEnum {
    COMMERCIAL("Commercial Event"),
    INFORMATIONAL("Informational Event");
    // other types

    private final String description;
}
