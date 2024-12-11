package com.github.therenegade.notification.manager.v1.controller;

import com.github.therenegade.notification.manager.dto.NotificationEventDTO;
import com.github.therenegade.notification.manager.dto.requests.CreateTimestampNotificationEventRequest;
import com.github.therenegade.notification.manager.mapper.NotificationEventMapper;
import com.github.therenegade.notification.manager.service.NotificationEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification/event")
@Tag(name = "Notification Event")
@RequiredArgsConstructor
public class NotificationEventController {

    private final NotificationEventService notificationEventService;
    private final NotificationEventMapper notificationEventMapper;

    @Operation(summary = "Fetching all stored notification events.",
            description = "The operation which fetches all stored notification events in the database."
    )
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationEventDTO>> getAllNotificationEvents() {
        return ResponseEntity.ok(notificationEventService.findAll().stream()
                .map(notificationEventMapper::toDto)
                .toList());
    }

    @Operation(summary = "Creation of timestamp scheduled notification event.",
            description = "The operation which creates the notification event which will be sent at specific timestamp.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "The request to create timestamp notification event"
            )
    )
    @PostMapping(value = "/timestamp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationEventDTO> createTimestampNotificationEvent(
            @RequestBody CreateTimestampNotificationEventRequest request
    ) {
        NotificationEventDTO createdEvent = notificationEventMapper.toDto(notificationEventService.createTimestampNotificationEvent(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEvent);
    }
}
