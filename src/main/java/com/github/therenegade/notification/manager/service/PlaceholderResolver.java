package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Placeholder;

import java.util.List;

public interface PlaceholderResolver {
    List<ResolvedPlaceholdersInformation> resolvePlaceholders(List<Placeholder> placeholders,
                                                              List<Integer> recipientsIds);
}
