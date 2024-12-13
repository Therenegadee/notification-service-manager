package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.dto.ResolvedPlaceholdersInformation;
import com.github.therenegade.notification.manager.entity.Placeholder;

import java.util.List;

/**
 * Resolver of {@link com.github.therenegade.notification.manager.entity.NotificationMessage} placeholders.
 */
public interface PlaceholderResolver {

    /**
     * Placeholder resolve function which aggregates all placeholders by service needs to be requested to fetch the information,
     * sends these requests and then aggregates all placeholders with their values by recipients.
     *
     * @param placeholders  placeholders to resolve.
     * @param recipientsIds ids of recipients.
     * @return information about placeholders' values for each recipient.
     */
    List<ResolvedPlaceholdersInformation> resolvePlaceholders(List<Placeholder> placeholders,
                                                              List<Integer> recipientsIds);
}
