package com.github.therenegade.notification.manager.v1.controller;

import com.github.therenegade.notification.manager.dto.NotificationChannelDTO;
import com.github.therenegade.notification.manager.dto.NotificationEventTypeDTO;
import com.github.therenegade.notification.manager.mapper.NotificationChannelMapper;
import com.github.therenegade.notification.manager.service.NotificationChannelService;
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
@RequestMapping("/notification/channel")
@Tag(name = "Notification Channel")
@RequiredArgsConstructor
public class NotificationChannelController {

    private final NotificationChannelService notificationChannelService;
    private final NotificationChannelMapper notificationChannelMapper;

    @Operation(summary = "Fetching all stored channels of notifications distribution.",
            description = "The operation which fetches all stored channels of notifications distribution in the database."
    )
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationChannelDTO>> getAllNotificationChannels() {
        return ResponseEntity.ok(notificationChannelService.findAll()
                .stream()
                .map(notificationChannelMapper::toDto)
                .toList());
    }
}
