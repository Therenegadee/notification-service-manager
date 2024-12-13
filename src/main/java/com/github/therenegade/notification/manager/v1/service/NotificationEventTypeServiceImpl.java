package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.NotificationEvent;
import com.github.therenegade.notification.manager.entity.NotificationEventType;
import com.github.therenegade.notification.manager.exceptions.NotFoundException;
import com.github.therenegade.notification.manager.repository.NotificationEventTypeRepository;
import com.github.therenegade.notification.manager.service.NotificationEventTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventTypeServiceImpl implements NotificationEventTypeService {

    private final NotificationEventTypeRepository notificationEventTypeRepository;

    @Override
    public NotificationEventType findById(Integer id) {
        return notificationEventTypeRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Event Type with id " + id + " wasn't found!";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    @Override
    public List<NotificationEventType> findAll() {
        return notificationEventTypeRepository.findAll();
    }
}
