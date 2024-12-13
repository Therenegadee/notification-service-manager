package com.github.therenegade.notification.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationEventDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "new_lecture")
    private String alias;

    @Schema(example = "New Lecture Opened")
    private String name;

    @Schema(example = "[1]")
    @ArraySchema(schema = @Schema(type = "integer"))
    private List<Integer> notificationMessagesIds;

    @Schema(example = "12")
    private Integer notificationEventTypeId;

    @Schema(example = "This event notifies users about the new lecture opened.")
    private String description;

    @Schema(example = "true")
    private Boolean isActive;

    @Schema(example = "TIMESTAMP")
    private NotificationExecutionType executionType;

    @Schema(example = "0 0 0 * * *")
    private String executeCron;

    @Schema(example = "2024-12-11T12:00:00+03:00")
    private OffsetDateTime executeTimestamp;
}
