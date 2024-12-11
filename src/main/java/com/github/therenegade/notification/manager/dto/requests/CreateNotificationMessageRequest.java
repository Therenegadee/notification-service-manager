package com.github.therenegade.notification.manager.dto.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNotificationMessageRequest {

    @NotNull(message = "Notification Event's id couldn't be null!")
    @Positive(message = "Notification Event's id couldn't be less or equals zero!")
    private Integer notificationEventId;

    @NotNull(message = "Message of Notification Event couldn't be null!")
    @NotEmpty(message = "Message of Notification Event couldn't be empty!")
    private String message;

    @Positive(message = "Notification Event Message's Target Channel Id couldn't be less or equals zero!")
    @NotNull(message = "Notification Event Message's Target Channel Id couldn't be null!")
    private Integer notificationChannelId;

    @Nullable
    private List<Integer> placeholdersIds;
}
