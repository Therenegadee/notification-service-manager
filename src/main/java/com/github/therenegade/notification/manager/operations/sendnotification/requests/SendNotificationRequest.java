package com.github.therenegade.notification.manager.operations.sendnotification.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class SendNotificationRequest {
    private final String recipientContactValue;
    private final String messageBody;
}
