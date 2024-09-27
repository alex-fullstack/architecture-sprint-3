package ru.yandex.practicum.controlpanel.dto;

import lombok.Data;

@Data
public class ReadDeviceDto {
    private Long id;
    private Long serialNumber;
    private ReadDeviceTypeDto type;
    private ReadHouseDto house;
    private ReadDeviceStatusDto status;
    private Long monolithId;
}