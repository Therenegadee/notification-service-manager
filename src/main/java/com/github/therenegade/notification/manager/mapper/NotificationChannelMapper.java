package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.NotificationChannelDTO;
import com.github.therenegade.notification.manager.entity.DistributionChannel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationChannelMapper {
    DistributionChannel toEntity(NotificationChannelDTO notificationChannelDTO);

    NotificationChannelDTO toDto(DistributionChannel distributionChannel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DistributionChannel partialUpdate(NotificationChannelDTO notificationChannelDTO, @MappingTarget DistributionChannel distributionChannel);
}