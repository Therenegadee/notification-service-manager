package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    @Query("SELECT s FROM Subscription s WHERE s.eventType.id = :notificationEventTypeId")
    List<Subscription> findSubscriptionsByNotificationEventType(Integer notificationEventTypeId);
}
