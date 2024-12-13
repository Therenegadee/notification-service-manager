package com.github.therenegade.notification.manager.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "Subscriber User Id couldn't be null!")
    @Positive(message = "Subscriber User Id couldn't be less or equals zero!")
    private Integer userId;

    @NotNull(message = "Notification Event's Type Id couldn't be null!")
    @Positive(message = "Notification Event's Type Id couldn't be less or equals zero!")
    private Integer notificationEventTypeId;

    @NotNull(message = "Notification Channel's Id couldn't be null!")
    @Positive(message = "Notification Channel's Id couldn't be less or equals zero!")
    private Integer notificationChannelId;

    @NotNull(message = "Contact Value couldn't be null!")
    @NotEmpty(message = "Contact Value couldn't be empty!")
    private String contactValue;
}
