package com.github.therenegade.notification.manager.v1.controller;

import com.github.therenegade.notification.manager.dto.PlaceholderDTO;
import com.github.therenegade.notification.manager.mapper.PlaceholderMapper;
import com.github.therenegade.notification.manager.service.PlaceholderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/placeholder")
@Tag(name = "Placeholder")
@RequiredArgsConstructor
public class PlaceholderController {

    private final PlaceholderService placeholderService;
    private final PlaceholderMapper placeholderMapper;

    @Operation(summary = "Fetching all stored placeholders.",
            description = "The operation which fetches all stored placeholders in the database."
    )
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PlaceholderDTO>> getAllPlaceholders() {
        return ResponseEntity.ok(placeholderService.findAll()
                .stream()
                .map(placeholderMapper::toDto)
                .toList());
    }
}
