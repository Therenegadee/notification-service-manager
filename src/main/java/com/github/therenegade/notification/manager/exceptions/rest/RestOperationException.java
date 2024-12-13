package com.github.therenegade.notification.manager.exceptions.rest;

public abstract class RestOperationException extends RuntimeException {
    public RestOperationException(String message) {
        super(message);
    }

    public RestOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
