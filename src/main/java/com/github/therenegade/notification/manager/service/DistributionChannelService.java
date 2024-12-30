package com.github.therenegade.notification.manager.service;

import com.github.therenegade.notification.manager.entity.DistributionChannel;

import java.util.List;

public interface DistributionChannelService {

    List<DistributionChannel> findAll();

    DistributionChannel findById(Integer id);
}
