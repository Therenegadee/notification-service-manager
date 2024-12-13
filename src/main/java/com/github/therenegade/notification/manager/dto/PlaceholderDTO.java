package com.github.therenegade.notification.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.therenegade.notification.manager.entity.enums.PlaceholderType;
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
public class PlaceholderDTO {

    @Schema(example = "1")
    private Integer id;

    private PlaceholderType alias;

    @Schema(example = "${recipient_full_name}")
    private String value;

    @Schema(example = "Notification recipient's full name.")
    private String description;
}
