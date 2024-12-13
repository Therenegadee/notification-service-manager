package com.github.therenegade.notification.manager.exceptions.sender;

import io.micrometer.common.util.StringUtils;

public class NoMessagesToSentExceptions extends NotificationEventSendException {

    private static final String DEFAULT_MESSAGE = "There were no messages to sent for the event!";

    public NoMessagesToSentExceptions(String message) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message);
    }

    public NoMessagesToSentExceptions(String message, Throwable cause) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message,
                cause);
    }
}
