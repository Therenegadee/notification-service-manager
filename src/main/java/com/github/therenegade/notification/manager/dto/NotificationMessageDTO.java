package com.github.therenegade.notification.manager.dto;

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
public class NotificationMessageDTO {

    private Integer id;

    private Integer notificationEventId;

    private Integer notificationChannelId;

    private String message;

    private List<Integer> placeholdersIds;
}
