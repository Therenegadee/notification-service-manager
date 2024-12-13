package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Integer>, JpaSpecificationExecutor<NotificationEvent> {

    @Query("""
            SELECT e FROM NotificationEvent e
            WHERE e.executionType = 'TIMESTAMP'
            AND e.isActive = true
            """
    )
    List<NotificationEvent> findActiveTimestampNotificationEvents();

    @Query("""
            SELECT e FROM NotificationEvent e
            WHERE e.executionType = 'CRON'
            AND e.isActive = true
            """
    )
    List<NotificationEvent> findActiveCronNotificationEvents();

    @EntityGraph(attributePaths = {"eventType", "messages"})
    @Override
    List<NotificationEvent> findAll(Specification<NotificationEvent> spec);

    static Specification<NotificationEvent> buildSpecification(@Nullable NotificationExecutionType executionType,
                                                               @Nullable Boolean isActive) {
        List<Specification<NotificationEvent>> specifications = new ArrayList<>();

        if (Objects.nonNull(executionType)) {
            specifications.add(
                    (Specification<NotificationEvent>) (event, query, criteriaBuilder) ->
                            criteriaBuilder.equal(event.get("executionType"), executionType)
            );
        }

        if (Objects.nonNull(isActive)) {
            specifications.add(
                    (Specification<NotificationEvent>) (event, query, criteriaBuilder) ->
                            criteriaBuilder.equal(event.get("isActive"), isActive)
            );
        }
        return specifications.stream().reduce(Specification::and).orElse(null);
    }
}
