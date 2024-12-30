package com.github.therenegade.notification.manager.v1.sender;

import com.github.therenegade.notification.manager.entity.enums.DistributionChannelType;
import com.github.therenegade.notification.manager.v1.sender.requests.SendTelegramNotificationInKafkaRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Sender of notifications with target channel type {@link DistributionChannelType#TELEGRAM} in it's Kafka topic.
 */
@Service
@Slf4j
public class SendTelegramNotificationInKafkaService extends SendNotificationInKafkaService<SendTelegramNotificationInKafkaRequest> {

    public SendTelegramNotificationInKafkaService(KafkaTemplate<String, SendTelegramNotificationInKafkaRequest> kafkaTemplate) {
        super(kafkaTemplate, DistributionChannelType.TELEGRAM);
    }
}
