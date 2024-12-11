package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.dto.requests.CreateNotificationMessageRequest;
import com.github.therenegade.notification.manager.dto.requests.CreateTimestampNotificationEventRequest;
import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import com.github.therenegade.notification.manager.exceptions.NotFoundException;
import com.github.therenegade.notification.manager.repository.NotificationEventRepository;
import com.github.therenegade.notification.manager.repository.NotificationEventTypeRepository;
import com.github.therenegade.notification.manager.service.NotificationEventService;
import com.github.therenegade.notification.manager.service.NotificationMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventServiceImpl implements NotificationEventService {

    private final NotificationEventRepository notificationEventRepository;
    private final NotificationEventTypeRepository notificationEventTypeRepository;
    private final NotificationMessageService notificationMessageService;

    @Override
    public List<NotificationEvent> findAll() {
        return notificationEventRepository.findAll();
    }

    @Transactional
    @Override
    public NotificationEvent createTimestampNotificationEvent(CreateTimestampNotificationEventRequest request) {
        NotificationEventType notificationEventType = notificationEventTypeRepository.findById(request.getNotificationEventTypeId())
                .orElseThrow(() -> {
                    String errorMessage = "Event Type with id = " + request.getNotificationEventTypeId() + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
        log.info("Start process of creation the notification event. [Name: {}; Alias: {}; Event Type Id: {}; Execute Timestamp: {}]",
                request.getName(), request.getAlias(), notificationEventType.getId(), request.getExecuteTimestamp());
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .name(request.getName())
                .alias(request.getAlias())
                .description(request.getDescription())
                .executionType(NotificationExecutionType.TIMESTAMP)
                .eventType(notificationEventType)
                .executeTimestamp(request.getExecuteTimestamp())
                .isActive(true)
                .build();
        notificationEvent = notificationEventRepository.save(notificationEvent);
        final int notificationEventId = notificationEvent.getId();
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
        notificationEvent.setMessages(notificationMessages);
        return notificationEvent;
    }
}
