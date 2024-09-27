package ru.yandex.practicum.telemetry.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReadTelemetryDto<T> {
    private ReadSensorDto sensor;
    private List<T> history;
}