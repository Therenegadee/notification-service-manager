package com.github.therenegade.notification.manager.operations.placeholder;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.github.therenegade.notification.manager.operations.placeholder.GetRecipientsInformationOperation.*;

/**
 * Now this service is the imitation of requesting recipients' (users') data from external API.
 * In the project with real user service API it'd be done using GraphQL.
 */
@Service
public class GetRecipientsInformationOperation extends AbstractGetPlaceholderNecessaryInformationOperation<GetRecipientsInformationRequest, GetRecipientsInformationResponse> {

    @Override
    public GetRecipientsInformationResponse execute(GetRecipientsInformationRequest request) {
        return GetRecipientsInformationResponse.builder()
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
    }

    @Data
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetRecipientsInformationRequest extends GetPlaceholderInfoRequest {
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
