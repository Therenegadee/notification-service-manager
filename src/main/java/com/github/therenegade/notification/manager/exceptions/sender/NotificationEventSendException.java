package com.github.therenegade.notification.manager.exceptions.sender;

public abstract class NotificationEventSendException extends RuntimeException {
    public NotificationEventSendException(String message) {
        super(message);
    }

    public NotificationEventSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
