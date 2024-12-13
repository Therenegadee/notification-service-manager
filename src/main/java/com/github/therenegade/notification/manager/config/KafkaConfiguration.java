package com.github.therenegade.notification.manager.config;

import com.github.therenegade.notification.manager.operations.sendnotification.requests.SendTelegramNotificationRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value("${integrations.kafka.cluster.addresses}")
    private String bootstrapAddresses;

    @Value("integrations.kafka.telegram.send.topic")
    private String sendTelegramNotificationTopic;

    @Bean("sendTelegramNotificationProducer")
    public KafkaTemplate<String, SendTelegramNotificationRequest> sendTelegramNotificationProducer() {
        KafkaTemplate<String, SendTelegramNotificationRequest> kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(getKafkaProducerConfig()));
        kafkaTemplate.setDefaultTopic(sendTelegramNotificationTopic);
        return kafkaTemplate;
    }

    private Map<String, Object> getKafkaProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddresses);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }
}

