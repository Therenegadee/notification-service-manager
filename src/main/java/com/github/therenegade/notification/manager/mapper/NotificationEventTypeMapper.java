package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationEventTypeDTO;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationEventTypeMapper {
    NotificationEventType toEntity(NotificationEventTypeDTO notificationEventTypeDTO);

    NotificationEventTypeDTO toDto(NotificationEventType notificationEventType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NotificationEventType partialUpdate(NotificationEventTypeDTO notificationEventTypeDTO, @MappingTarget NotificationEventType notificationEventType);
}