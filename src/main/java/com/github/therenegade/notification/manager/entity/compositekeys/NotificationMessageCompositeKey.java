package com.github.therenegade.notification.manager.entity.compositekeys;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class NotificationMessageCompositeKey implements Serializable {
    private Integer notificationEvent;
    private Integer notificationChannel;
}
