package com.github.therenegade.notification.manager.exceptions.rest;

import io.micrometer.common.util.StringUtils;

public class NotFoundException extends RestOperationException {

    private static final String DEFAULT_MESSAGE = "Requested resource wasn't found!";

    public NotFoundException(String message) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message,
                cause);
    }
}
