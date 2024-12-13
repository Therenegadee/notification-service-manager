package com.github.therenegade.notification.manager.dto.requests;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTimestampNotificationEventRequest {

    @Schema(example = "new_lecture", maxLength = 25)
    @NotEmpty(message = "Alias of Notification Event couldn't be empty!")
    @NotNull(message = "Alias of Notification Event couldn't be null!")
    private String alias;

    @Schema(example = "New Lecture Opened")
    @NotEmpty(message = "Name of Notification Event couldn't be empty!")
    @NotNull(message = "Name of Notification Event couldn't be null!")
    private String name;

    @Schema(example = "This event notifies users about the new lecture opened.")
    private String description;

    @Schema(example = "2")
    @Positive(message = "Notification Event's Type Id couldn't be less or equals zero!")
    @NotNull(message = "Notification Event's Type Id couldn't be null!")
    private Integer notificationEventTypeId;

    @Schema(example = "2024-12-11T12:00:00+03:00")
    @FutureOrPresent(message = "Execute Timestamp of Notification Event couldn't be in past!")
    @NotNull(message = "Execute Timestamp of Notification Event couldn't be null!")
    private OffsetDateTime executeTimestamp;

    private List<NotificationMessageForNotificationEvent> notificationMessages;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NotificationMessageForNotificationEvent {

        @Schema(example = "Hey, ${recipient_full_name}! New lecture is ready to study. Open it as soon as you have free time and enjoy new lesson!")
        @NotNull(message = "Message of Notification Event couldn't be null!")
        @NotEmpty(message = "Message of Notification Event couldn't be empty!")
        private String message;

        @Schema(example = "1")
        @Positive(message = "Notification Event Message's Target Channel Id couldn't be less or equals zero!")
        @NotNull(message = "Notification Event Message's Target Channel Id couldn't be null!")
        private Integer notificationChannelId;

        @Schema(example = "[2]")
        @ArraySchema(schema = @Schema(type = "integer"))
        private List<Integer> placeholdersIds;
    }
}
