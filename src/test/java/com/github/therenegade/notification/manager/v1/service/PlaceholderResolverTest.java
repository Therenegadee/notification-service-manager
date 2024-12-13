package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.client.UserServiceClient;
import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.entity.enums.PlaceholderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PlaceholderResolverTest {

    @InjectMocks
    private PlaceholderResolver placeholderResolver;
    @Mock
    private UserServiceClient userServiceClient;

    @Test
    void resolvePlaceholdersTest_placeholdersCorrectlyResolved_returnsValidResultForUsers() {
        // Given
        int user_1_id = 1;
        UserServiceClient.GetRecipientsInformationResponse.RecipientInformation user_1_info = UserServiceClient.GetRecipientsInformationResponse.RecipientInformation
                .builder()
                .recipientId(user_1_id)
                .recipientFullName("Alexeev Alexey Alexeevich")
                .build();

        int user_2_id = 2;
        UserServiceClient.GetRecipientsInformationResponse.RecipientInformation user_2_info = UserServiceClient.GetRecipientsInformationResponse.RecipientInformation
                .builder()
                .recipientId(user_2_id)
                .recipientFullName("Ivanov Ivan Ivanovich")
                .build();

        List<Integer> inputUsersIds = List.of(user_1_id, user_2_id);

        List<Placeholder> inputPlaceholders = new ArrayList<>();
        Placeholder placeholder = new Placeholder(PlaceholderType.RECIPIENT_FULL_NAME);
        inputPlaceholders.add(placeholder);

        when(userServiceClient.fetchRecipientData(any(UserServiceClient.GetRecipientsInformationRequest.class)))
                .thenReturn(List.of(user_1_info, user_2_info));

        // When
        List<ResolvedPlaceholdersInformation> resolvedPlaceholdersInformation =
                placeholderResolver.resolvePlaceholders(inputPlaceholders, inputUsersIds);

        // Then
        assertThat(resolvedPlaceholdersInformation)
                .isNotEmpty();
        assertThat(resolvedPlaceholdersInformation)
                .hasSize(2);
        assertThat(resolvedPlaceholdersInformation)
                .anyMatch(info -> info.getRecipientId().equals(user_1_id)
                        && info.getResolvedPlaceholderValues().get(PlaceholderType.RECIPIENT_FULL_NAME.getPlaceholderName())
                        .equals(user_1_info.getRecipientFullName())
                );
        assertThat(resolvedPlaceholdersInformation)
                .anyMatch(info -> info.getRecipientId().equals(user_2_id)
                        && info.getResolvedPlaceholderValues().get(PlaceholderType.RECIPIENT_FULL_NAME.getPlaceholderName())
                        .equals(user_2_info.getRecipientFullName())
                );
    }
}
