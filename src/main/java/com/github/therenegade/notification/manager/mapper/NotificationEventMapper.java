package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationEventDTO;
import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
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
    NotificationEvent toEntity(NotificationEventDTO notificationEventDTO);

    @Mapping(source = "eventType", target = "notificationEventTypeId", qualifiedByName = "convertEventTypeToId")
    @Mapping(source = "messages", target = "notificationMessagesIds", qualifiedByName = "convertMessagesToIds")
    NotificationEventDTO toDto(NotificationEvent notificationEvent);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NotificationEvent partialUpdate(NotificationEventDTO notificationEventDTO,
                                    @MappingTarget NotificationEvent notificationEvent);

    @Named("convertEventTypeToId")
    default Integer convertEventTypeToId(NotificationEventType eventType) {
        return eventType.getId();
    }

    @Named("convertEventTypeIdToEntity")
    default NotificationEventType convertEventTypeIdToEntity(Integer eventTypeId) {
        return new NotificationEventType(eventTypeId);
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