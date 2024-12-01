package com.github.therenegade.notification.manager.entity.enums;

import lombok.Getter;

@Getter
public enum PlaceholderType {
    RECIPIENT_NAME("recipient_name", "Notification recipient's name.", GetPlaceholderInfoQueriedService.RECIPIENT_USER_SERVICE),
    RECIPIENT_FULL_NAME("recipient_full_name", "Notification recipient's full name.", GetPlaceholderInfoQueriedService.RECIPIENT_USER_SERVICE),
    RECIPIENT_BIRTHDAY("recipient_birthday", "Notification recipient's birthday date.", GetPlaceholderInfoQueriedService.RECIPIENT_USER_SERVICE);
    // etc

    private final String placeholderName;
    private final String description;
    private final GetPlaceholderInfoQueriedService queriedService;

    PlaceholderType(String placeholderName,
                    String description,
                    GetPlaceholderInfoQueriedService queriedService) {
        this.placeholderName = placeholderName;
        this.description = description;
        this.queriedService = queriedService;
    }
}
