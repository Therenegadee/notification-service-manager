package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.NotificationEventTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_type", schema = "notifications")
@Getter
public class NotificationEventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private NotificationEventTypeEnum alias;

    private String description;

    public NotificationEventType(NotificationEventTypeEnum eventTypeEnum) {
        this.alias = eventTypeEnum;
        this.description = eventTypeEnum.getDescription();
    }

    public NotificationEventType(Integer id) {
        this.id = id;
    }

    public NotificationEventType() {}
}
