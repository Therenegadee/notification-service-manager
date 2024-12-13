package com.github.therenegade.notification.manager.v1.sender.requests;

public class SendTelegramNotificationInKafkaRequest extends SendNotificationInKafkaRequest {

    public SendTelegramNotificationInKafkaRequest(String recipientContactValue, String message) {
        super(recipientContactValue, message);
    }
}
