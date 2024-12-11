package com.github.therenegade.notification.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.therenegade.notification.manager.entity.enums.NotificationEventTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationEventTypeDTO {

    @Schema(example = "1")
    private Integer id;

    private NotificationEventTypeEnum alias;

    @Schema(example = "Commercial Event")
    private String description;
}
