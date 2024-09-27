package ru.yandex.practicum.controlpanel.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateDeviceDto {
    private Long type;
    private Long house;
    private Long serialNumber;
    private List<CreateSensorDto> sensors;
}