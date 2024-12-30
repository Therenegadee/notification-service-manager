package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.DistributionChannelType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "distribution_channel", schema = "notifications")
@Getter
public class DistributionChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private DistributionChannelType alias;

    public DistributionChannel(DistributionChannelType distributionChannelType) {
        this.name = distributionChannelType.getName();
        this.alias = distributionChannelType;
    }

    public DistributionChannel(Integer id) {
        this.id = id;
    }

    public DistributionChannel() {

    }
}
