package ru.yandex.practicum.telemetry.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReadTemperatureTelemetryDto {
    private Long id;
    private double value;
    private LocalDateTime timestamp;
}