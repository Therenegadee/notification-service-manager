package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.requests.CreateNotificationMessageRequest;
import com.github.therenegade.notification.manager.dto.requests.CreateTimestampNotificationEventRequest;
import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.NotificationType;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import com.github.therenegade.notification.manager.exceptions.rest.NotFoundException;
import com.github.therenegade.notification.manager.repository.NotificationRepository;
import com.github.therenegade.notification.manager.repository.NotificationTypeRepository;
import com.github.therenegade.notification.manager.service.NotificationService;
import com.github.therenegade.notification.manager.service.NotificationMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationMessageService notificationMessageService;

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    @Override
    public Notification createTimestampNotificationEvent(CreateTimestampNotificationEventRequest request) {
        NotificationType notificationType = notificationTypeRepository.findById(request.getNotificationEventTypeId())
                .orElseThrow(() -> {
                    String errorMessage = "Event Type with id = " + request.getNotificationEventTypeId() + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
        log.info("Start process of creation the notification event. [Name: {}; Alias: {}; Event Type Id: {}; Execute Timestamp: {}]",
                request.getName(), request.getAlias(), notificationType.getId(), request.getExecuteTimestamp());
        Notification notification = Notification.builder()
                .name(request.getName())
                .alias(request.getAlias())
                .description(request.getDescription())
                .executionType(NotificationExecutionType.TIMESTAMP)
                .eventType(notificationType)
                .executeTimestamp(request.getExecuteTimestamp())
                .isActive(true)
                .build();
        notification = notificationRepository.save(notification);
        final int notificationEventId = notification.getId();
        log.info("Notification event was successfully saved with id = {}.", notificationEventId);
        List<NotificationMessage> notificationMessages = request.getNotificationMessages()
                .stream()
                .map(eventMessageInfo -> {
                    var createNotificationMessageRequest = CreateNotificationMessageRequest.builder()
                            .notificationEventId(notificationEventId)
                            .message(eventMessageInfo.getMessage())
                            .notificationChannelId(eventMessageInfo.getNotificationChannelId())
                            .placeholdersIds(eventMessageInfo.getPlaceholdersIds())
                            .build();
                    return notificationMessageService.createNotificationMessage(createNotificationMessageRequest);
                })
                .toList();
        log.info("All Notification Event messages was successfully saved!");
        notification.setMessages(notificationMessages);
        return notification;
    }
}
