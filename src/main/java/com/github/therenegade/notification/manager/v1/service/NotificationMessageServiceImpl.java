package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.requests.CreateNotificationMessageRequest;
import com.github.therenegade.notification.manager.entity.DistributionChannel;
import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.exceptions.rest.NotFoundException;
import com.github.therenegade.notification.manager.repository.DistributionChannelRepository;
import com.github.therenegade.notification.manager.repository.NotificationMessageRepository;
import com.github.therenegade.notification.manager.repository.PlaceholderRepository;
import com.github.therenegade.notification.manager.service.NotificationMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationMessageServiceImpl implements NotificationMessageService {

    private final NotificationMessageRepository notificationMessageRepository;
    private final DistributionChannelRepository distributionChannelRepository;
    private final PlaceholderRepository placeholderRepository;

    @Override
    @Transactional
    public NotificationMessage createNotificationMessage(CreateNotificationMessageRequest request) {
        DistributionChannel distributionChannel = distributionChannelRepository.findById(request.getNotificationChannelId())
                .orElseThrow(() -> {
                    String errorMessage = "Notification Channel with id = " + request.getNotificationChannelId() + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
        log.info("Start process of creation the notification event's message. [Event Id: {}; Notification Channel Id: {}; Message: \"{}\"]",
                request.getNotificationEventId(), request.getNotificationChannelId(), request.getMessage());
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .notification(Notification.builder().id(request.getNotificationEventId()).build())
                .distributionChannel(distributionChannel)
                .message(request.getMessage())
                .build();
        if (!request.getPlaceholdersIds().isEmpty()) {
            log.debug("{} placeholders' ids were pointed to link the message with. Ids list: {}.",
                    request.getPlaceholdersIds().size(), Arrays.toString(request.getPlaceholdersIds().toArray()));
            List<Placeholder> placeholders = placeholderRepository.findAllById(request.getPlaceholdersIds());
            notificationMessage.setPlaceholders(placeholders);
        }
        notificationMessage = notificationMessageRepository.save(notificationMessage);
        log.info("Notification Message was successfully saved with id = {}.", notificationMessage.getId());
        return notificationMessage;
    }
}
