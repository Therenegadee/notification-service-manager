package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationChannelDTO;
import com.github.therenegade.notification.manager.entity.NotificationChannel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationChannelMapper {
    NotificationChannel toEntity(NotificationChannelDTO notificationChannelDTO);

    NotificationChannelDTO toDto(NotificationChannel notificationChannel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NotificationChannel partialUpdate(NotificationChannelDTO notificationChannelDTO, @MappingTarget NotificationChannel notificationChannel);
}