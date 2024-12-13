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
@Table(name = "event_send_history", schema = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEventSendHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_event_id", referencedColumnName = "id")
    private NotificationEvent notificationEvent;

    @Column(name = "stage")
    @Enumerated(EnumType.STRING)
    private NotificationSendStage stage;

    @Column(name = "start_Time")
    private OffsetDateTime startTime;

    @Column(name = "finish_time")
    private OffsetDateTime finishTime;

    @OneToMany(mappedBy = "notificationEventSendHistory", cascade = CascadeType.ALL)
    private Set<NotificationEventSendHistoryError> sendingErrors;

    public void addSendingError(NotificationEventSendHistoryError error) {
        error.setNotificationEventSendHistory(this);
        this.sendingErrors.add(error);
    }
}
