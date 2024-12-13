package com.github.therenegade.notification.manager.operations.sendnotification;

import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.operations.sendnotification.requests.SendTelegramNotificationInKafkaRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Sender of notifications with target channel type {@link NotificationChannelType#TELEGRAM} in it's Kafka topic.
 */
@Service
@Slf4j
public class SendTelegramNotificationInKafkaOperation extends SendNotificationInKafkaOperation<SendTelegramNotificationInKafkaRequest> {

    public SendTelegramNotificationInKafkaOperation(KafkaTemplate<String, SendTelegramNotificationInKafkaRequest> kafkaTemplate) {
        super(kafkaTemplate, NotificationChannelType.TELEGRAM);
    }
}
