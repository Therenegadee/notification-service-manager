package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.SubscriptionDTO;
import com.github.therenegade.notification.manager.entity.NotificationChannel;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
import com.github.therenegade.notification.manager.entity.Subscription;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubscriptionMapper {

    @Mapping(source = "notificationChannelId", target = "notificationChannel", qualifiedByName = "convertNotificationChannelIdToEntity")
    @Mapping(source = "notificationEventTypeId", target = "eventType", qualifiedByName = "convertEventTypeIdToEntity")
    Subscription toEntity(SubscriptionDTO subscriptionDTO);

    @Mapping(source = "notificationChannel", target = "notificationChannelId", qualifiedByName = "convertNotificationChannelToId")
    @Mapping(source = "eventType", target = "notificationEventTypeId", qualifiedByName = "convertEventTypeToId")
    SubscriptionDTO toDto(Subscription subscription);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Subscription partialUpdate(SubscriptionDTO subscriptionDTO, @MappingTarget Subscription subscription);

    @Named("convertEventTypeToId")
    default Integer convertEventTypeToId(NotificationEventType eventType) {
        return eventType.getId();
    }

    @Named("convertEventTypeIdToEntity")
    default NotificationEventType convertEventTypeIdToEntity(Integer eventTypeId) {
        return new NotificationEventType(eventTypeId);
    }

    @Named("convertNotificationChannelToId")
    default Integer convertNotificationChannelToId(NotificationChannel channel) {
        return channel.getId();
    }

    @Named("convertNotificationChannelIdToEntity")
    default NotificationChannel convertNotificationChannelIdToEntity(Integer channelId) {
        return new NotificationChannel(channelId);
    }
}