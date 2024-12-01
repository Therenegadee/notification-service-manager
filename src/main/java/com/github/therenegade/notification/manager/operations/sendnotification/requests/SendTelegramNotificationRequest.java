package com.github.therenegade.notification.manager.operations.sendnotification.requests;

public class SendTelegramNotificationRequest extends SendNotificationRequest {

    public SendTelegramNotificationRequest(String recipientContactValue, String message) {
        super(recipientContactValue, message);
    }
}
