package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.NotificationChannel;
import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventSendHistory;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Subscription;
import com.github.therenegade.notification.manager.entity.enums.NotificationChannelType;
import com.github.therenegade.notification.manager.entity.enums.NotificationEventTypeEnum;
import com.github.therenegade.notification.manager.entity.enums.NotificationExecutionType;
import com.github.therenegade.notification.manager.exceptions.sender.NoMessagesToSentException;
import com.github.therenegade.notification.manager.exceptions.sender.NoSubscriptionsForEventException;
import com.github.therenegade.notification.manager.exceptions.sender.NotificationSendingErrorFinishedException;
import com.github.therenegade.notification.manager.repository.NotificationEventRepository;
import com.github.therenegade.notification.manager.repository.NotificationEventSendHistoryRepository;
import com.github.therenegade.notification.manager.repository.SubscriptionRepository;
import com.github.therenegade.notification.manager.v1.sender.SendTelegramNotificationInKafkaService;
import com.github.therenegade.notification.manager.v1.sender.requests.SendNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.requests.SendTelegramNotificationInKafkaRequest;
import com.github.therenegade.notification.manager.v1.sender.results.SendNotificationInKafkaResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class NotificationEventSendServiceTest {

    @InjectMocks
    private NotificationEventSendService notificationEventSendService;
    @Mock
    private SendTelegramNotificationInKafkaService sendTelegramNotificationOperation;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private PlaceholderResolver placeholderResolver;
    @Mock
    private NotificationEventSendHistoryRepository notificationEventSendHistoryRepository;
    @Mock
    private NotificationEventRepository notificationEventRepository;

    @Test
    void sendNotificationTest_eventHasEmptyMessagesList_throwsNotificationSendingErrorFinishedExceptionWithRootCauseNoMessagesToSentException() {
        // Given
        NotificationEvent inputEvent = NotificationEvent.builder()
                .id(1)
                .isActive(true)
                .eventType(new NotificationEventType(NotificationEventTypeEnum.INFORMATIONAL))
                .executionType(NotificationExecutionType.TIMESTAMP)
                .messages(Collections.emptyList())
                .build();

        // When
        // Then
        assertThatThrownBy(() -> notificationEventSendService.sendNotification(inputEvent))
                .isInstanceOf(NotificationSendingErrorFinishedException.class)
                .hasRootCauseInstanceOf(NoMessagesToSentException.class)
                .hasMessageContaining("Notification event with id = " + inputEvent.getId() + " has no message to sent!");
        verify(notificationEventSendHistoryRepository, atMostOnce())
                .save(any(NotificationEventSendHistory.class));
    }

    @Test
    void sendNotificationTest_unexpectedExceptionDuringExecution_throwsNotificationSendingErrorFinishedExceptionWithRootOriginalCause() {
        // Given
        Exception exceptionToBeThrown = new IllegalArgumentException("Something went wrong!");
        int eventTypeId = 1;
        NotificationEvent inputEvent = NotificationEvent.builder()
                .id(1)
                .isActive(true)
                .eventType(new NotificationEventType(eventTypeId))
                .executionType(NotificationExecutionType.TIMESTAMP)
                .messages(List.of(
                                NotificationMessage.builder()
                                        .notificationChannel(new NotificationChannel(NotificationChannelType.TELEGRAM))
                                        .build()
                        )
                )
                .build();
        when(subscriptionRepository.findSubscriptionsByNotificationEventType(eq(eventTypeId)))
                .thenThrow(exceptionToBeThrown);

        // When
        // Then
        assertThatThrownBy(() -> notificationEventSendService.sendNotification(inputEvent))
                .isInstanceOf(NotificationSendingErrorFinishedException.class)
                .hasRootCauseInstanceOf(exceptionToBeThrown.getClass())
                .hasMessageContaining(exceptionToBeThrown.getMessage());
        verify(notificationEventSendHistoryRepository, times(2))
                .save(any(NotificationEventSendHistory.class));
    }

    @Test
    void sendNotificationTest_subscriptionsListIsEmpty_throwsNotificationSendingErrorFinishedExceptionWithRootCauseNoSubscriptionsForEventException() {
        // Given
        NotificationChannel notificationChannel = new NotificationChannel(NotificationChannelType.TELEGRAM);
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .notificationChannel(notificationChannel)
                .build();
        NotificationEventType eventType = new NotificationEventType(NotificationEventTypeEnum.INFORMATIONAL);
        NotificationEvent inputEvent = NotificationEvent.builder()
                .id(1)
                .isActive(true)
                .eventType(eventType)
                .executionType(NotificationExecutionType.TIMESTAMP)
                .messages(List.of(notificationMessage))
                .build();

        when(subscriptionRepository.findSubscriptionsByNotificationEventType(eq(eventType.getId())))
                .thenReturn(Collections.emptyList());

        // When
        // Then
        assertThatThrownBy(() -> notificationEventSendService.sendNotification(inputEvent))
                .isInstanceOf(NotificationSendingErrorFinishedException.class)
                .hasRootCauseInstanceOf(NoSubscriptionsForEventException.class)
                .hasMessageContaining("There are no any subscriptions was found for event with id = " + inputEvent.getId());
        verify(notificationEventSendHistoryRepository, times(2))
                .save(any(NotificationEventSendHistory.class));
    }

    @Test
    void sendNotificationTest_subscriptionsListForTelegramChannelIsEmpty_returnsEmptyList() {
        // Given
        NotificationChannel notificationChannel = new NotificationChannel(NotificationChannelType.TELEGRAM);
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .notificationChannel(notificationChannel)
                .build();
        NotificationEventType eventType = new NotificationEventType(NotificationEventTypeEnum.INFORMATIONAL);
        NotificationEvent inputEvent = NotificationEvent.builder()
                .id(1)
                .isActive(true)
                .eventType(eventType)
                .executionType(NotificationExecutionType.TIMESTAMP)
                .messages(List.of(notificationMessage))
                .build();

        when(subscriptionRepository.findSubscriptionsByNotificationEventType(eq(eventType.getId())))
                .thenReturn(List.of(Subscription.builder()
                        .notificationChannel(new NotificationChannel(NotificationChannelType.EMAIL))
                        .build())
                );

        // When
        List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> result = notificationEventSendService.sendNotification(inputEvent);

        // Then
        assertThat(result)
                .isEmpty();
        verify(notificationEventSendHistoryRepository, times(2))
                .save(any(NotificationEventSendHistory.class));
    }

    @Test
    void sendNotificationTest_necessaryInformationForMessageWithoutPlaceholderFound_returnsValidSendResultList() {
        // Given
        NotificationChannel notificationChannel = new NotificationChannel(NotificationChannelType.TELEGRAM);
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message("Hello!")
                .notificationChannel(notificationChannel)
                .placeholders(Collections.emptyList())
                .build();
        NotificationEventType eventType = new NotificationEventType(NotificationEventTypeEnum.INFORMATIONAL);
        NotificationEvent inputEvent = NotificationEvent.builder()
                .id(1)
                .isActive(true)
                .eventType(eventType)
                .executionType(NotificationExecutionType.TIMESTAMP)
                .messages(List.of(notificationMessage))
                .build();

        int userId = 1;
        Subscription subscription = Subscription.builder()
                .id(1)
                .notificationChannel(notificationChannel)
                .eventType(eventType)
                .contactValue("123")
                .userId(userId)
                .build();

        when(subscriptionRepository.findSubscriptionsByNotificationEventType(eq(eventType.getId())))
                .thenReturn(List.of(subscription));
        when(sendTelegramNotificationOperation.sendNotification(any(SendTelegramNotificationInKafkaRequest.class)))
                .thenReturn(SendNotificationInKafkaResult.<SendTelegramNotificationInKafkaRequest>builder()
                        .isNotificationSent(true)
                        .sendResult(new SendResult<>(new ProducerRecord<>("", new SendTelegramNotificationInKafkaRequest(subscription.getContactValue(), notificationMessage.getMessage())),
                                mock(RecordMetadata.class)))
                        .build());

        // When
        List<SendNotificationInKafkaResult<? extends SendNotificationInKafkaRequest>> result = notificationEventSendService.sendNotification(inputEvent);

        // Then
        assertThat(result)
                .isNotEmpty();
        assertThat(result)
                .hasSize(1);
        verify(notificationEventSendHistoryRepository, times(2))
                .save(any(NotificationEventSendHistory.class));
        verify(sendTelegramNotificationOperation, atMostOnce())
                .sendNotification(any(SendTelegramNotificationInKafkaRequest.class));
    }

}
