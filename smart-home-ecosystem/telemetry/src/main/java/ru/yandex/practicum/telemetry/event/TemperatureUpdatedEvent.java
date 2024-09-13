package ru.yandex.practicum.telemetry.event;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TemperatureUpdatedEvent {
    private Long sensorSerialNumber;
    private double value;
    private LocalDateTime timestamp;
}