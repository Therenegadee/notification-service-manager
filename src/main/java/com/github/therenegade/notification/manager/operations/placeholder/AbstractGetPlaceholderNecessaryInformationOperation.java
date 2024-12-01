package com.github.therenegade.notification.manager.operations.placeholder;

public abstract class AbstractGetPlaceholderNecessaryInformationOperation<T extends GetPlaceholderInfoRequest, R> {

    public abstract R execute(T request);
}
