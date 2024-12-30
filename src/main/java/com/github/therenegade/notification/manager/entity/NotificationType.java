package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.NotificationTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "notification_type", schema = "notifications")
@Getter
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum alias;

    private String description;

    public NotificationType(NotificationTypeEnum eventTypeEnum) {
        this.alias = eventTypeEnum;
        this.description = eventTypeEnum.getDescription();
    }

    public NotificationType(Integer id) {
        this.id = id;
    }

    public NotificationType() {}
}
