package com.github.therenegade.notification.manager.operations.sendnotification.results;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.kafka.support.SendResult;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationInKafkaResult<T> {
    private boolean isNotificationSent;
    @Nullable
    private String errorMessage;
    @Nullable
    private SendResult<String, T> sendResult;
    @Nullable
    private Exception exceptionOccurred;
}