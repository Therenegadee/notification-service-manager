package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.NotificationSendStage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "sending_queue", schema = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSendQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private Notification notification;

    @Column(name = "stage")
    @Enumerated(EnumType.STRING)
    private NotificationSendStage stage;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "finish_time")
    private OffsetDateTime finishTime;

    @OneToMany(mappedBy = "notificationSendQueue", cascade = CascadeType.ALL)
    private Set<NotificationSendError> sendingErrors;

    public void addSendingError(NotificationSendError error) {
        error.setNotificationSendQueue(this);
        this.sendingErrors.add(error);
    }
}
