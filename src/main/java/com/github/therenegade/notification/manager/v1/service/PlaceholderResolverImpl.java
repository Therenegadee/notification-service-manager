package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.operations.placeholder.GetPlaceholderInfoRequest;
import com.github.therenegade.notification.manager.operations.placeholder.GetPlaceholderNecessaryInformationOperation;
import com.github.therenegade.notification.manager.operations.placeholder.GetRecipientsInformationOperation;
import com.github.therenegade.notification.manager.operations.placeholder.enums.GetPlaceholderInfoQueriedService;
import com.github.therenegade.notification.manager.service.PlaceholderResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlaceholderResolverImpl implements PlaceholderResolver {

    private final GetRecipientsInformationOperation getRecipientsInformationOperation;
    private final Map<GetPlaceholderInfoQueriedService, GetPlaceholderNecessaryInformationOperation> fetchPlaceholderDataOperationsByService;

    public PlaceholderResolverImpl(GetRecipientsInformationOperation getRecipientsInformationOperation,
                                   List<GetPlaceholderNecessaryInformationOperation> getPlaceholdersDataInfoOperations) {
        this.getRecipientsInformationOperation = getRecipientsInformationOperation;
        this.fetchPlaceholderDataOperationsByService = getPlaceholdersDataInfoOperations.stream()
                .collect(Collectors.toMap(GetPlaceholderNecessaryInformationOperation::getQueriedService, operation -> operation));
    }


    // other operations declaration

    @Override
    public List<ResolvedPlaceholdersInformation> resolvePlaceholders(List<Placeholder> placeholders,
                                                                     List<Integer> recipientsIds) {
        Map<GetPlaceholderInfoQueriedService, List<Placeholder>> requestsByServices = placeholders
                .stream()
                .collect(Collectors.groupingBy(placeholder -> placeholder.getAlias().getQueriedService()));

        List<Map<Integer, ResolvedPlaceholdersInformation>> resolvedPlaceholdersInformation = new ArrayList<>();
        GetPlaceholderInfoRequest getPlaceholderInfoRequest = GetPlaceholderInfoRequest.builder()
                .recipientsIds(recipientsIds)
                .placeholders(placeholders)
                .build();
        requestsByServices.keySet().forEach(queriedService ->
                resolvedPlaceholdersInformation.add(getRecipientsInformationOperation.execute(getPlaceholderInfoRequest))
        );

        Map<Integer, ResolvedPlaceholdersInformation> mergedMap = new HashMap<>();
        for (Map<Integer, ResolvedPlaceholdersInformation> resolvedPlaceholdersByUserId : resolvedPlaceholdersInformation) {
            resolvedPlaceholdersByUserId.forEach((key, value) -> {
                mergedMap.computeIfAbsent(key, mapping -> new ResolvedPlaceholdersInformation(key, new HashMap<>()));
                mergedMap.get(key).getResolvedPlaceholderValues().putAll(value.getResolvedPlaceholderValues());
            });
        }

        return mergedMap.values().stream().toList();
    }
}
