package ru.yandex.practicum.controlpanel.dto;

import lombok.Data;

@Data
public class ReadHouseDto {
    private Long id;
    private Long userId;
    private String adress;
    private ReadHouseTypeDto type;
}