package ru.yandex.practicum.telemetry.dto;

import lombok.Data;

@Data
public class CreateSensorDto {
    private Long serialNumber;
    private String type;
    private String unit;
}