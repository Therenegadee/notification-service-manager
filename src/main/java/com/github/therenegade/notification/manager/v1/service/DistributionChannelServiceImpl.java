package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.DistributionChannel;
import com.github.therenegade.notification.manager.exceptions.rest.NotFoundException;
import com.github.therenegade.notification.manager.repository.DistributionChannelRepository;
import com.github.therenegade.notification.manager.service.DistributionChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionChannelServiceImpl implements DistributionChannelService {

    private final DistributionChannelRepository distributionChannelRepository;

    @Override
    public DistributionChannel findById(Integer id) {
        return distributionChannelRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Notification Channel with id " + id + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    @Override
    public List<DistributionChannel> findAll() {
        return distributionChannelRepository.findAll();
    }
}
