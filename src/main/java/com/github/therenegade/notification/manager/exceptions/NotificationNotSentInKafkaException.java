package com.github.therenegade.notification.manager.exceptions;

import io.micrometer.common.util.StringUtils;

public class NotificationNotSentInKafkaException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "For some reason the notification wasn't sent into Kafka Topic.";

    public NotificationNotSentInKafkaException(String message) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message);
    }

    public NotificationNotSentInKafkaException(String message, Throwable cause) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message,
                cause);
    }
}
