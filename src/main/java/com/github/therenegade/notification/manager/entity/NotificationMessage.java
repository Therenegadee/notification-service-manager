package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.compositekeys.NotificationMessageCompositeKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Table(name = "message", schema = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "notification_event_id")
    private NotificationEvent notificationEvent;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "notification_channel_id")
    private NotificationChannel notificationChannel;

    private String message;

    @ManyToMany
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(schema = "notifications", name = "notification_message_placeholder",
            joinColumns = @JoinColumn(name = "notification_message_id"),
            inverseJoinColumns = @JoinColumn(name = "placeholder_id"))
    private List<Placeholder> placeholders;
}
