package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Integer>, JpaSpecificationExecutor<NotificationMessage> {

    @EntityGraph(attributePaths = {"notificationEvent.eventType", "notificationChannel", "placeholders"})
    @Override
    List<NotificationMessage> findAll(Specification<NotificationMessage> spec);

    static Specification<NotificationMessage> buildSpecification(@Nullable List<Integer> eventsIds) {
        List<Specification<NotificationMessage>> specifications = new ArrayList<>();

        if (Objects.nonNull(eventsIds) && !eventsIds.isEmpty()) {
            specifications.add(
                    (Specification<NotificationMessage>) (message, query, criteriaBuilder) ->
                            message.get("notificationEvent").get("id").in(eventsIds)
            );
        }
        return specifications.stream().reduce(Specification::and).orElse(null);
    }
}
