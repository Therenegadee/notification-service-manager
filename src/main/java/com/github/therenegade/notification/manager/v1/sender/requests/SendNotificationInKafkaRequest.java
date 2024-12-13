package com.github.therenegade.notification.manager.v1.sender.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class SendNotificationInKafkaRequest {
    private final String recipientContactValue;
    private final String messageBody;
}
