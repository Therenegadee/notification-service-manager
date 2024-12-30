package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationEventDTO;
import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.NotificationType;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationEventMapper {

    @Mapping(source = "notificationEventTypeId", target = "eventType", qualifiedByName = "convertEventTypeIdToEntity")
    @Mapping(source = "notificationMessagesIds", target = "messages", qualifiedByName = "convertMessagesIdsToEntities")
    Notification toEntity(NotificationEventDTO notificationEventDTO);

    @Mapping(source = "eventType", target = "notificationEventTypeId", qualifiedByName = "convertEventTypeToId")
    @Mapping(source = "messages", target = "notificationMessagesIds", qualifiedByName = "convertMessagesToIds")
    NotificationEventDTO toDto(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(NotificationEventDTO notificationEventDTO,
                               @MappingTarget Notification notification);

    @Named("convertEventTypeToId")
    default Integer convertEventTypeToId(NotificationType eventType) {
        return eventType.getId();
    }

    @Named("convertEventTypeIdToEntity")
    default NotificationType convertEventTypeIdToEntity(Integer eventTypeId) {
        return new NotificationType(eventTypeId);
    }

    @Named("convertMessagesToIds")
    default List<Integer> convertMessagesToIds(List<NotificationMessage> messages) {
        return messages.stream()
                .map(NotificationMessage::getId)
                .toList();
    }

    @Named("convertMessagesIdsToEntities")
    default List<NotificationMessage> convertMessagesIdsToEntities(List<Integer> messagesIds) {
        return messagesIds.stream()
                .map(id -> NotificationMessage.builder().id(id).build())
                .toList();
    }
}