package com.github.therenegade.notification.manager.dto;

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
public class SubscriptionDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "121")
    private Integer userId;

    @Schema(example = "2")
    private Integer notificationEventTypeId;

    @Schema(example = "1")
    private Integer notificationChannelId;

    @Schema(example = "434135")
    private String contactValue;
}
