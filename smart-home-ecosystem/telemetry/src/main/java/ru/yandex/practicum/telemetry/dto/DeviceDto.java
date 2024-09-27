package ru.yandex.practicum.telemetry.dto;

import lombok.Data;

@Data
public class DeviceDto {
    private Long id;
    private Long serialNumber;
    private DeviceTypeDto type;
    private HouseDto house;
    private DeviceStatusDto status;
    private Long monolithId;
}