package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.NotificationType;
import com.github.therenegade.notification.manager.exceptions.rest.NotFoundException;
import com.github.therenegade.notification.manager.repository.NotificationTypeRepository;
import com.github.therenegade.notification.manager.service.NotificationTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTypeServiceImpl implements NotificationTypeService {

    private final NotificationTypeRepository notificationTypeRepository;

    @Override
    public NotificationType findById(Integer id) {
        return notificationTypeRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Event Type with id " + id + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    @Override
    public List<NotificationType> findAll() {
        return notificationTypeRepository.findAll();
    }
}
