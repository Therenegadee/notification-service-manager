package com.github.therenegade.notification.manager.operations.placeholder;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.entity.enums.PlaceholderType;
import com.github.therenegade.notification.manager.operations.placeholder.enums.GetPlaceholderInfoQueriedService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.therenegade.notification.manager.entity.enums.PlaceholderType.RECIPIENT_BIRTHDAY;
import static com.github.therenegade.notification.manager.entity.enums.PlaceholderType.RECIPIENT_FULL_NAME;
import static com.github.therenegade.notification.manager.entity.enums.PlaceholderType.RECIPIENT_NAME;
import static com.github.therenegade.notification.manager.operations.placeholder.GetRecipientsInformationOperation.*;

/**
 * Now this service is the imitation of requesting recipients' (users') data from external API.
 * In the project with real user service API it'd be done using GraphQL.
 */
@Service
@Slf4j
public class GetRecipientsInformationOperation extends GetPlaceholderNecessaryInformationOperation {

    public GetRecipientsInformationOperation() {
        super(GetPlaceholderInfoQueriedService.RECIPIENT_USER_SERVICE);
    }

    @Override
    public Map<Integer, ResolvedPlaceholdersInformation> execute(GetPlaceholderInfoRequest request) {
        GetRecipientsInformationRequest getRecipientsInformationRequest = new GetRecipientsInformationRequest();
        getRecipientsInformationRequest.setRecipientsIds(request.getRecipientsIds());
        Map<PlaceholderType, Placeholder> placeholdersByType = request.getPlaceholders().stream()
                .collect(Collectors.toMap(Placeholder::getAlias, placeholder -> placeholder));
        placeholdersByType.keySet()
                .forEach(placeholderType -> {
                    switch (placeholderType) {
                        case RECIPIENT_NAME -> getRecipientsInformationRequest.setRecipientNameNecessary(true);
                        case RECIPIENT_FULL_NAME -> getRecipientsInformationRequest.setRecipientFullNameNecessary(true);
                        case RECIPIENT_BIRTHDAY -> getRecipientsInformationRequest.setRecipientBirthdayNecessary(true);
                        default ->
                                log.warn("The incorrect type of placeholder which couldn't be resolved: {}.", placeholderType);
                    }
                });

        /* Imitation of request */
        GetRecipientsInformationResponse getRecipientsInformationResponse = GetRecipientsInformationResponse.builder()
                .recipientsInformation(getRecipientsInformationRequest.getRecipientsIds()
                        .stream()
                        .map(recipientId -> GetRecipientsInformationResponse.RecipientInformation.builder()
                                .recipientId(recipientId)
                                .recipientName(getRecipientsInformationRequest.isRecipientNameNecessary ? "James" : null)
                                .recipientFullName(getRecipientsInformationRequest.isRecipientFullNameNecessary() ? "James Gosling" : null)
                                .recipientBirthday(getRecipientsInformationRequest.isRecipientBirthdayNecessary() ? LocalDate.of(1955, 5, 19) : null)
                                .build())
                        .toList())
                .build();
        /* */

        Map<Integer, GetRecipientsInformationResponse.RecipientInformation> recipientInformationById = getRecipientsInformationResponse
                .getRecipientsInformation()
                .stream()
                .collect(Collectors.toMap(GetRecipientsInformationResponse.RecipientInformation::getRecipientId, recipientInformation -> recipientInformation));
        return recipientInformationById.entrySet()
                .stream()
                .map(entry -> {
                    Integer recipientId = entry.getKey();
                    GetRecipientsInformationResponse.RecipientInformation recipientInformation = entry.getValue();
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

    @Data
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetRecipientsInformationRequest {
        private List<Integer> recipientsIds;
        private boolean isRecipientNameNecessary;
        private boolean isRecipientFullNameNecessary;
        private boolean isRecipientBirthdayNecessary;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetRecipientsInformationResponse {
        private List<RecipientInformation> recipientsInformation;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class RecipientInformation {
            private Integer recipientId;
            @Nullable
            private String recipientName;
            @Nullable
            private String recipientFullName;
            @Nullable
            private LocalDate recipientBirthday;
        }
    }
}
