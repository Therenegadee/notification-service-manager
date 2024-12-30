package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationEventTypeDTO;
import com.github.therenegade.notification.manager.entity.NotificationType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationEventTypeMapper {
    NotificationType toEntity(NotificationEventTypeDTO notificationEventTypeDTO);

    NotificationEventTypeDTO toDto(NotificationType notificationType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NotificationType partialUpdate(NotificationEventTypeDTO notificationEventTypeDTO, @MappingTarget NotificationType notificationType);
}