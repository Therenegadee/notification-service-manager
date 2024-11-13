package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.compositekeys.NotificationMessageCompositeKey;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message", schema = "notifications")
@IdClass(NotificationMessageCompositeKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessage {

    private NotificationEvent notificationEvent;
    private NotificationChannel notificationChannel;
    private String message;
}
