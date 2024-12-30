package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "notification", schema = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alias")
    private String alias;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "notification")
    @Fetch(FetchMode.SUBSELECT)
    private List<NotificationMessage> messages;

    @ManyToOne
    @JoinColumn(name = "notification_type_id", referencedColumnName = "id")
    private NotificationType eventType;

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
