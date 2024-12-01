package com.github.therenegade.notification.manager.operations.sendnotification;

import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.operations.sendnotification.requests.SendTelegramNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendTelegramNotificationOperation extends SendNotificationOperation<SendTelegramNotificationRequest> {

    public SendTelegramNotificationOperation(KafkaTemplate<String, SendTelegramNotificationRequest> kafkaTemplate) {
        super(kafkaTemplate, NotificationChannelType.TELEGRAM);
    }
}
