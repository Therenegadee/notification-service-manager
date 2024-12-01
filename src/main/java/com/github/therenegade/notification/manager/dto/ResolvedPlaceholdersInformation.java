package com.github.therenegade.notification.manager.dto;

import com.github.therenegade.notification.manager.entity.Placeholder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResolvedPlaceholdersInformation {
    private Integer recipientId;
    private Map<String, String> resolvedPlaceholderValues;
}
