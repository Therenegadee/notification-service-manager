package com.github.therenegade.notification.manager.operations.sendnotification.requests;

public class SendTelegramNotificationInKafkaRequest extends SendNotificationInKafkaRequest {

    public SendTelegramNotificationInKafkaRequest(String recipientContactValue, String message) {
        super(recipientContactValue, message);
    }
}
