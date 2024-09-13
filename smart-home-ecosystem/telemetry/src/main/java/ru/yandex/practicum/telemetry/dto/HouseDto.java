package ru.yandex.practicum.telemetry.dto;

import lombok.Data;

@Data
public class HouseDto {
    private Long id;
    private Long userId;
    private String adress;
    private HouseTypeDto type;
}