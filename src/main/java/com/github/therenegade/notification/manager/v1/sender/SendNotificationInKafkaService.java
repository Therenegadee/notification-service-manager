package com.github.therenegade.notification.manager.v1.sender;

import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.v1.sender.requests.SendNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.results.SendNotificationInKafkaResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Objects;

/**
 * Abstract sender of notifications in Kafka topics which will be read by Notification Sender Service.
 *
 * @param <T> type of request to send the notification message.
 */
public abstract class SendNotificationInKafkaService<T extends SendNotificationInKafkaRequest> {

    protected Logger log = LoggerFactory.getLogger(getClass());
    protected final KafkaTemplate<String, T> kafkaProducer;
    protected final NotificationChannelType notificationChannelType;

    public SendNotificationInKafkaService(KafkaTemplate<String, T> kafkaProducer,
                                          NotificationChannelType notificationChannelType) {
        this.kafkaProducer = kafkaProducer;
        this.notificationChannelType = notificationChannelType;
    }

    public SendNotificationInKafkaResult<T> sendNotification(T request) {
        log.info("Sending prepared notification message in Kafka Topic \"{}\". Recipient: {}. Message: {}.",
                kafkaProducer.getDefaultTopic(), request.getRecipientContactValue(), request.getMessageBody());
        try {
            SendResult<String, T> sendResult = kafkaProducer.send(kafkaProducer.getDefaultTopic(), request).join();
            if (Objects.isNull(sendResult)) {
                String errorMessage = String.format("The result of sending the prepared notification message was null!" +
                                "\sVery probably the message wasn't sent in Kafka. Kafka Topic: %s. Recipient: %s. Message: %s.",
                        kafkaProducer.getDefaultTopic(), request.getRecipientContactValue(), request.getMessageBody());
                log.error(errorMessage);
                return SendNotificationInKafkaResult.<T>builder()
                        .isNotificationSent(false)
                        .errorMessage(errorMessage)
                        .build();
            }
            log.info("The prepared notification message was sent in Kafka topic \"{}\" for recipient (contact value: {})." +
                            "\sPartition: {}. Offset: {}.", kafkaProducer.getDefaultTopic(), request.getRecipientContactValue(),
                    sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset());
            return SendNotificationInKafkaResult.<T>builder()
                    .isNotificationSent(true)
                    .sendResult(sendResult)
                    .build();
        } catch (Exception exception) {
            String errorMessage = String.format("The error occurred when trying to send the prepared notification message." +
                    "Kafka Topic: %s. Original Error Message: %s.", kafkaProducer.getDefaultTopic(), ExceptionUtils.getMessage(exception));
            log.error(errorMessage + "\nStackTrace: {}", ExceptionUtils.getStackTrace(exception));
            // for future i think it'd be a good solution to send the error message in audit kafka topic
            return SendNotificationInKafkaResult.<T>builder()
                    .isNotificationSent(false)
                    .errorMessage(errorMessage)
                    .exceptionOccurred(exception)
                    .build();
        }
    }
}
