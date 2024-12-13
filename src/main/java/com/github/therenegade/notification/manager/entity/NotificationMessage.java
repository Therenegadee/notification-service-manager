package com.github.therenegade.notification.manager.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToMany(cascade = {CascadeType.REFRESH})
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(schema = "notifications", name = "notification_message_placeholder",
            joinColumns = @JoinColumn(name = "notification_message_id"),
            inverseJoinColumns = @JoinColumn(name = "placeholder_id"))
    private List<Placeholder> placeholders;
}
