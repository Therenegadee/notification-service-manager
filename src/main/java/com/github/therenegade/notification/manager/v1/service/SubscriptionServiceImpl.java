package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.requests.CreateSubscriptionRequest;
import com.github.therenegade.notification.manager.entity.DistributionChannel;
import com.github.therenegade.notification.manager.entity.NotificationType;
import com.github.therenegade.notification.manager.entity.Subscription;
import com.github.therenegade.notification.manager.repository.SubscriptionRepository;
import com.github.therenegade.notification.manager.service.DistributionChannelService;
import com.github.therenegade.notification.manager.service.NotificationTypeService;
import com.github.therenegade.notification.manager.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final DistributionChannelService distributionChannelService;
    private final NotificationTypeService notificationTypeService;

    @Override
    public List<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription createSubscription(CreateSubscriptionRequest request) {
        log.info("Start process of creation the subscription. [User ID: {}; Event Type: {}; Channel Id: {}; Contact Value: {}]",
                request.getUserId(), request.getNotificationEventTypeId(), request.getNotificationChannelId(), request.getContactValue());

        DistributionChannel distributionChannel = distributionChannelService.findById(request.getNotificationChannelId());
        NotificationType notificationType = notificationTypeService.findById(request.getNotificationEventTypeId());

        Subscription subscription = Subscription.builder()
                .userId(request.getUserId())
                .contactValue(request.getContactValue())
                .eventType(notificationType)
                .distributionChannel(distributionChannel)
                .build();

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription was successfully saved with id = {}.", subscription.getId());

        return subscription;
    }
}
