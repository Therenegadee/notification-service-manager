package com.github.therenegade.notification.manager.entity.enums;

import com.github.therenegade.notification.enums.QueriedServiceType;
import lombok.Getter;

@Getter
public enum PlaceholderType {
    RECIPIENT_NAME("recipient_name", "Notification recipient's name.", QueriedServiceType.RECIPIENT_USER_SERVICE),
    RECIPIENT_FULL_NAME("recipient_full_name", "Notification recipient's full name.", QueriedServiceType.RECIPIENT_USER_SERVICE),
    RECIPIENT_BIRTHDAY("recipient_birthday", "Notification recipient's birthday date.", QueriedServiceType.RECIPIENT_USER_SERVICE);
    // etc

    private final String placeholderName;
    private final String description;
    private final QueriedServiceType queriedService;

    PlaceholderType(String placeholderName,
                    String description,
                    QueriedServiceType queriedService) {
        this.placeholderName = placeholderName;
        this.description = description;
        this.queriedService = queriedService;
    }
}
