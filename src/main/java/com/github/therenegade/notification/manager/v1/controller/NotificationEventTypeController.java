package com.github.therenegade.notification.manager.v1.controller;

import com.github.therenegade.notification.manager.dto.NotificationEventTypeDTO;
import com.github.therenegade.notification.manager.mapper.NotificationEventTypeMapper;
import com.github.therenegade.notification.manager.service.NotificationEventTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification/event/type")
@Tag(name = "Notification Event Type")
@RequiredArgsConstructor
public class NotificationEventTypeController {

    private final NotificationEventTypeService notificationEventTypeService;
    private final NotificationEventTypeMapper notificationEventTypeMapper;

    @Operation(summary = "Fetching all stored notification events' types.",
            description = "The operation which fetches all stored notification events' types in the database."
    )
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationEventTypeDTO>> getAllNotificationEventTypes() {
        return ResponseEntity.ok(notificationEventTypeService.findAll()
                .stream()
                .map(notificationEventTypeMapper::toDto)
                .toList());
    }

    @Operation(summary = "Fetching notification event type by id.",
            description = "The operation which fetches stored notification event type in the database by it's id."
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationEventTypeDTO> getNotificationEventTypeById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(notificationEventTypeMapper.toDto(notificationEventTypeService.findById(id)));
    }
}
