package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.requests.CreateSubscriptionRequest;
import com.github.therenegade.notification.manager.entity.Subscription;

import java.util.List;

public interface SubscriptionService {

    List<Subscription> findAll();

    Subscription createSubscription(CreateSubscriptionRequest request);
}
