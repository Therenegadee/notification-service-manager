package com.github.therenegade.notification.manager.exceptions.sender;

import io.micrometer.common.util.StringUtils;

public class NoSubscriptionsForEventException extends NotificationEventSendException {

    private static final String DEFAULT_MESSAGE = "There were no subscriptions for the event!";

    public NoSubscriptionsForEventException(String message) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message);
    }

    public NoSubscriptionsForEventException(String message, Throwable cause) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message,
                cause);
    }
}
