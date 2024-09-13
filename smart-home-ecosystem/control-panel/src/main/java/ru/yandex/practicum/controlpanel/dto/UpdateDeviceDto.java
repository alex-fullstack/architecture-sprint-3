package ru.yandex.practicum.controlpanel.dto;

import java.util.List;

import lombok.Data;

@Data
public class UpdateDeviceDto {
    private Long house;
    private Long serialNumber;
    private List<UpdateSensorDto> sensors;
}