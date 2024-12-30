package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationMessageDTO;
import com.github.therenegade.notification.manager.entity.DistributionChannel;
import com.github.therenegade.notification.manager.entity.Notification;
import com.github.therenegade.notification.manager.entity.NotificationMessage;
import com.github.therenegade.notification.manager.entity.Placeholder;
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
public interface NotificationMessageMapper {

    @Mapping(source = "notificationEventId", target = "notificationEvent", qualifiedByName = "convertEventIdToEntity")
    @Mapping(source = "notificationChannelId", target = "notificationChannel", qualifiedByName = "convertNotificationChannelIdToEntity")
    @Mapping(source = "placeholdersIds", target = "placeholders", qualifiedByName = "convertPlaceholdersIdsToEntities")
    NotificationMessage toEntity(NotificationMessageDTO notificationMessageDTO);

    @Mapping(source = "notificationEvent", target = "notificationEventId", qualifiedByName = "convertEventToId")
    @Mapping(source = "notificationChannel", target = "notificationChannelId", qualifiedByName = "convertNotificationChannelToId")
    @Mapping(source = "placeholders", target = "placeholdersIds", qualifiedByName = "convertPlaceholdersToIds")
    NotificationMessageDTO toDto(NotificationMessage notificationMessage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NotificationMessage partialUpdate(NotificationMessageDTO notificationMessageDTO,
                                      @MappingTarget NotificationMessage notificationMessage);

    @Named("convertEventToId")
    default Integer convertEventToId(Notification event) {
        return event.getId();
    }

    @Named("convertEventIdToEntity")
    default Notification convertEventIdToEntity(Integer eventId) {
        return Notification.builder().id(eventId).build();
    }

    @Named("convertNotificationChannelToId")
    default Integer convertNotificationChannelToId(DistributionChannel channel) {
        return channel.getId();
    }

    @Named("convertNotificationChannelIdToEntity")
    default DistributionChannel convertNotificationChannelIdToEntity(Integer channelId) {
        return new DistributionChannel(channelId);
    }

    @Named("convertPlaceholdersToIds")
    default List<Integer> convertMessagesToIds(List<Placeholder> placeholders) {
        return placeholders.stream()
                .map(Placeholder::getId)
                .toList();
    }

    @Named("convertPlaceholdersIdsToEntities")
    default List<Placeholder> convertMessagesIdsToEntities(List<Integer> placeholdersIds) {
        return placeholdersIds.stream()
                .map(Placeholder::new)
                .toList();
    }
}