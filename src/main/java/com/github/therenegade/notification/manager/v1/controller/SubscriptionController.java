package com.github.therenegade.notification.manager.v1.controller;

import com.github.therenegade.notification.manager.dto.SubscriptionDTO;
import com.github.therenegade.notification.manager.dto.requests.CreateSubscriptionRequest;
import com.github.therenegade.notification.manager.mapper.SubscriptionMapper;
import com.github.therenegade.notification.manager.service.SubscriptionService;
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
@RequestMapping("/subscription")
@Tag(name = "Subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;

    @Operation(summary = "Fetching all stored notification events' subscriptions.",
            description = "The operation which fetches all stored notification events' subscriptions in the database."
    )
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.findAll().stream()
                .map(subscriptionMapper::toDto)
                .toList());
    }

    @Operation(summary = "Creation of notification event's subscription.",
            description = "The operation which creates the notification event's subscription.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "The request to create notification event's subscription"
            )
    )
    @PostMapping(value = "/timestamp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody CreateSubscriptionRequest request) {
        SubscriptionDTO createdSubscription = subscriptionMapper.toDto(subscriptionService.createSubscription(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSubscription);
    }
}
