package com.github.therenegade.notification.manager.dto;

import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
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
public class NotificationChannelDTO {
    private Integer id;

    private String name;

    private NotificationChannelType alias;
}
