package com.github.therenegade.notification.manager.entity.enums;

import lombok.Getter;

@Getter
public enum DistributionChannelType {
    TELEGRAM("Telegram"),
    EMAIL("Email");
    // other channels

    private final String name;

    DistributionChannelType(String name) {
        this.name = name;
    }
}
