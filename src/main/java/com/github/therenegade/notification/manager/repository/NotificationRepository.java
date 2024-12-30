package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {

    @Query("""
            SELECT e FROM NotificationEvent e
            WHERE e.executionType = 'TIMESTAMP'
            AND e.isActive = true
            """
    )
    List<Notification> findActiveTimestampNotificationEvents();

    @Query("""
            SELECT e FROM NotificationEvent e
            WHERE e.executionType = 'CRON'
            AND e.isActive = true
            """
    )
    List<Notification> findActiveCronNotificationEvents();

    @EntityGraph(attributePaths = {"eventType", "messages"})
    @Override
    List<Notification> findAll(Specification<Notification> spec);

    static Specification<Notification> buildSpecification(@Nullable NotificationExecutionType executionType,
                                                          @Nullable Boolean isActive,
                                                          @Nullable OffsetDateTime beforeExecuteTimeStamp) {
        List<Specification<Notification>> specifications = new ArrayList<>();

        if (Objects.nonNull(executionType)) {
            specifications.add(
                    (Specification<Notification>) (event, query, criteriaBuilder) ->
                            criteriaBuilder.equal(event.get("executionType"), executionType)
            );
        }

        if (Objects.nonNull(isActive)) {
            specifications.add(
                    (Specification<Notification>) (event, query, criteriaBuilder) ->
                            criteriaBuilder.equal(event.get("isActive"), isActive)
            );
        }

        if (Objects.nonNull(beforeExecuteTimeStamp)) {
            specifications.add(
                    (Specification<Notification>) (event, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(event.get("executeTimestamp"), beforeExecuteTimeStamp)
            );
        }
        return specifications.stream().reduce(Specification::and).orElse(null);
    }
}
