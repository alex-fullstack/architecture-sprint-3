package ru.yandex.practicum.controlpanel.dto;

import lombok.Data;

@Data
public class CreateSensorDto {
    private Long serialNumber;
    private String type;
    private String unit;
}