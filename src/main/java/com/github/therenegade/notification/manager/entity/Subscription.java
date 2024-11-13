package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.compositekeys.SubscriptionCompositeKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "subscription", schema = "notifications")
@IdClass(SubscriptionCompositeKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "notification_event_id", referencedColumnName = "id")
    private NotificationEvent event;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "notification_channel_id", referencedColumnName = "id")
    private NotificationChannel notificationChannel;
}
