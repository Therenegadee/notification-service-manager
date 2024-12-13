package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.client.UserServiceClient;
import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.entity.enums.PlaceholderType;
import com.github.therenegade.notification.enums.QueriedServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.therenegade.notification.manager.entity.enums.PlaceholderType.RECIPIENT_BIRTHDAY;
import static com.github.therenegade.notification.manager.entity.enums.PlaceholderType.RECIPIENT_FULL_NAME;
import static com.github.therenegade.notification.manager.entity.enums.PlaceholderType.RECIPIENT_NAME;

/**
 * Resolver of {@link com.github.therenegade.notification.manager.entity.NotificationMessage} placeholders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceholderResolver {

    private final UserServiceClient userServiceClient;


    /**
     * Placeholder resolve function which aggregates all placeholders by service needs to be requested to fetch the information,
     * sends these requests and then aggregates all placeholders with their values by recipients.
     *
     * @param placeholders  placeholders to resolve.
     * @param recipientsIds ids of recipients.
     * @return information about placeholders' values for each recipient.
     */
    public List<ResolvedPlaceholdersInformation> resolvePlaceholders(List<Placeholder> placeholders,
                                                                     List<Integer> recipientsIds) {
        Map<QueriedServiceType, List<Placeholder>> requestsByServices = placeholders
                .stream()
                .collect(Collectors.groupingBy(placeholder -> placeholder.getAlias().getQueriedService()));

        List<Map<Integer, ResolvedPlaceholdersInformation>> resolvedPlaceholdersInformation = new ArrayList<>();
        requestsByServices.forEach((queriedService, placeholderList) -> {
            Map<Integer, ResolvedPlaceholdersInformation> serviceResolvedPlaceholderData = switch (queriedService) {
                case RECIPIENT_USER_SERVICE -> resolveRecipientPlaceholderData(placeholderList, recipientsIds);
            };
            resolvedPlaceholdersInformation.add(serviceResolvedPlaceholderData);

        });

        Map<Integer, ResolvedPlaceholdersInformation> mergedMap = new HashMap<>();
        for (Map<Integer, ResolvedPlaceholdersInformation> resolvedPlaceholdersByUserId : resolvedPlaceholdersInformation) {
            resolvedPlaceholdersByUserId.forEach((key, value) -> {
                mergedMap.computeIfAbsent(key, mapping -> new ResolvedPlaceholdersInformation(key, new HashMap<>()));
                mergedMap.get(key).getResolvedPlaceholderValues().putAll(value.getResolvedPlaceholderValues());
            });
        }

        return mergedMap.values().stream().toList();
    }

    private Map<Integer, ResolvedPlaceholdersInformation> resolveRecipientPlaceholderData(List<Placeholder> recipientPlaceholders,
                                                                                          List<Integer> recipientsIds) {
        UserServiceClient.GetRecipientsInformationRequest request = new UserServiceClient.GetRecipientsInformationRequest();
        request.setRecipientsIds(recipientsIds);

        Map<PlaceholderType, Placeholder> placeholdersByType = recipientPlaceholders.stream()
                .collect(Collectors.toMap(Placeholder::getAlias, placeholder -> placeholder));
        placeholdersByType.keySet()
                .forEach(placeholderType -> {
                    switch (placeholderType) {
                        case RECIPIENT_NAME -> request.setRecipientNameNecessary(true);
                        case RECIPIENT_FULL_NAME -> request.setRecipientFullNameNecessary(true);
                        case RECIPIENT_BIRTHDAY -> request.setRecipientBirthdayNecessary(true);
                        default ->
                                log.warn("The incorrect type of placeholder which couldn't be resolved: {}.", placeholderType);
                    }
                });

        List<UserServiceClient.GetRecipientsInformationResponse.RecipientInformation> recipientsInformation = userServiceClient.fetchRecipientData(request);

        Map<Integer, UserServiceClient.GetRecipientsInformationResponse.RecipientInformation> recipientInformationById = recipientsInformation
                .stream()
                .collect(Collectors.toMap(UserServiceClient.GetRecipientsInformationResponse.RecipientInformation::getRecipientId, recipientInformation -> recipientInformation));

        return recipientInformationById.entrySet()
                .stream()
                .map(entry -> {
                    Integer recipientId = entry.getKey();
                    UserServiceClient.GetRecipientsInformationResponse.RecipientInformation recipientInformation = entry.getValue();
                    Map<String, String> resolvedServicePlaceholders = new HashMap<>();
                    Optional.ofNullable(recipientInformation.getRecipientName())
                            .ifPresent(recipientName -> resolvedServicePlaceholders.put(placeholdersByType.get(RECIPIENT_NAME).getValue(), recipientName));
                    Optional.ofNullable(recipientInformation.getRecipientFullName())
                            .ifPresent(recipientFullName -> resolvedServicePlaceholders.put(placeholdersByType.get(RECIPIENT_FULL_NAME).getValue(), recipientFullName));
                    Optional.ofNullable(recipientInformation.getRecipientBirthday())
                            .ifPresent(recipientBirthday -> resolvedServicePlaceholders.put(placeholdersByType.get(RECIPIENT_BIRTHDAY).getValue(), recipientBirthday.toString()));
                    return ResolvedPlaceholdersInformation.builder()
                            .recipientId(recipientId)
                            .resolvedPlaceholderValues(resolvedServicePlaceholders)
                            .build();
                })
                .collect(Collectors.toMap(ResolvedPlaceholdersInformation::getRecipientId, resolvedPlaceholdersInformation -> resolvedPlaceholdersInformation));

    }
}
