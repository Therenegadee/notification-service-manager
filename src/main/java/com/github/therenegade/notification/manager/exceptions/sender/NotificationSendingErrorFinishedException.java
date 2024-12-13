package com.github.therenegade.notification.manager.exceptions.sender;

import io.micrometer.common.util.StringUtils;

public class NotificationSendingErrorFinishedException extends NotificationEventSendException {

    private static final String DEFAULT_MESSAGE = "The unexpected exception was thrown during sending the notifications in" +
            "\sKafka topics.";


    public NotificationSendingErrorFinishedException(String message) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message);
    }

    public NotificationSendingErrorFinishedException(String message, Throwable cause) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message,
                cause);
    }
}
