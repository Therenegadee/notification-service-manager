package com.github.therenegade.notification.manager.operations.placeholder;

/**
 * Abstract Operation of retrieving the information about placeholders.
 *
 * @param <T> request with the information needs to be fetched from called service.
 * @param <R> response with the information requested in {@link GetPlaceholderInfoRequest}.
 */
public abstract class AbstractGetPlaceholderNecessaryInformationOperation<T extends GetPlaceholderInfoRequest, R> {

    public abstract R execute(T request);
}
