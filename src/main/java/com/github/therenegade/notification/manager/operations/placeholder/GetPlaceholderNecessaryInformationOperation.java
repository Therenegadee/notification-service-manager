package com.github.therenegade.notification.manager.operations.placeholder;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.operations.placeholder.enums.GetPlaceholderInfoQueriedService;
import lombok.Getter;

import java.util.Map;

/**
 * Abstract Operation of retrieving the information about placeholders.
 */
@Getter
public abstract class GetPlaceholderNecessaryInformationOperation {

    private final GetPlaceholderInfoQueriedService queriedService;

    public GetPlaceholderNecessaryInformationOperation(GetPlaceholderInfoQueriedService queriedService) {
        this.queriedService = queriedService;
    }

    public abstract Map<Integer, ResolvedPlaceholdersInformation> execute(GetPlaceholderInfoRequest request);
}
