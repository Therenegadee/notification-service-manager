package com.github.therenegade.notification.manager.client;

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
import java.util.List;

/**
 * Now this service is the imitation of requesting recipients' (users') data from external API.
 * In the project with real user service API it'd be done using GraphQL.
 */
@Service
@Slf4j
public class UserServiceClient {

    public List<GetRecipientsInformationResponse.RecipientInformation> fetchRecipientData(GetRecipientsInformationRequest request) {
        /* Imitation of sending the request */
        GetRecipientsInformationResponse getRecipientsInformationResponse = GetRecipientsInformationResponse.builder()
                .recipientsInformation(request.getRecipientsIds()
                        .stream()
                        .map(recipientId -> GetRecipientsInformationResponse.RecipientInformation.builder()
                                .recipientId(recipientId)
                                .recipientName(request.isRecipientNameNecessary ? "James" : null)
                                .recipientFullName(request.isRecipientFullNameNecessary() ? "James Gosling" : null)
                                .recipientBirthday(request.isRecipientBirthdayNecessary() ? LocalDate.of(1955, 5, 19) : null)
                                .build())
                        .toList())
                .build();
        /* */
        return getRecipientsInformationResponse.getRecipientsInformation();
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
        private List<GetRecipientsInformationResponse.RecipientInformation> recipientsInformation;

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
