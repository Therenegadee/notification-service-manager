package com.github.therenegade.notification.manager.mapper;

import com.github.therenegade.notification.manager.dto.PlaceholderDTO;
import com.github.therenegade.notification.manager.entity.Placeholder;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlaceholderMapper {
    Placeholder toEntity(PlaceholderDTO placeholderDTO);

    PlaceholderDTO toDto(Placeholder placeholder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Placeholder partialUpdate(PlaceholderDTO placeholderDTO, @MappingTarget Placeholder placeholder);
}