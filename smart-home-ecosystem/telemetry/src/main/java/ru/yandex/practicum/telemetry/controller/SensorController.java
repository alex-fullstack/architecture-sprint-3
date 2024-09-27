package ru.yandex.practicum.telemetry.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.telemetry.dto.ReadTelemetryDto;
import ru.yandex.practicum.telemetry.service.SensorService;

@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;
    private static final Logger logger = LoggerFactory.getLogger(SensorController.class);

    @GetMapping("/{id}/telemetry")
    public ResponseEntity<ReadTelemetryDto<?>> getTelemetry(@PathVariable("id") Long id) {
        logger.info("Fetching sensor telemetry with id {}", id);
        return ResponseEntity.ok(sensorService.getTelemetry(id));
    }

}
