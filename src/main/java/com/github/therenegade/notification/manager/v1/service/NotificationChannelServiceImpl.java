package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.NotificationChannel;
import com.github.therenegade.notification.manager.exceptions.rest.NotFoundException;
import com.github.therenegade.notification.manager.repository.NotificationChannelRepository;
import com.github.therenegade.notification.manager.service.NotificationChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationChannelServiceImpl implements NotificationChannelService {

    private final NotificationChannelRepository notificationChannelRepository;

    @Override
    public NotificationChannel findById(Integer id) {
        return notificationChannelRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Notification Channel with id " + id + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    @Override
    public List<NotificationChannel> findAll() {
        return notificationChannelRepository.findAll();
    }
}
