package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "event", schema = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String alias;

    private String name;

    @OneToMany(mappedBy = "notificationEvent", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<NotificationMessage> messages;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "event_type_id", referencedColumnName = "id")
    private NotificationEventType eventType;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_type")
    private NotificationExecutionType executionType;

    @Column(name = "execute_cron")
    private String executeCron;

    @Column(name = "execute_timestamp")
    private OffsetDateTime executeTimestamp;
}
