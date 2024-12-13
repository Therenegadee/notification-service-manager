package com.github.therenegade.notification.manager.v1.service;

import com.github.therenegade.notification.manager.entity.Placeholder;
import com.github.therenegade.notification.manager.repository.PlaceholderRepository;
import com.github.therenegade.notification.manager.service.PlaceholderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceholderServiceImpl implements PlaceholderService {

    private final PlaceholderRepository placeholderRepository;

    @Override
    public List<Placeholder> findAll() {
        return placeholderRepository.findAll();
    }
}
