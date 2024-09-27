package ru.yandex.practicum.telemetry.dto;

import lombok.Data;

@Data
public class ReadSensorDto {
    private Long id;
    private Long serialNumber;
    private ReadSensorTypeDto type;
    private ReadUnitDto unit;
    private Long deviceId;
}