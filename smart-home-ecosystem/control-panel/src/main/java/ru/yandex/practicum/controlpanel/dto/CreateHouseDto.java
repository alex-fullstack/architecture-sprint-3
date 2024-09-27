package ru.yandex.practicum.controlpanel.dto;

import lombok.Data;

@Data
public class CreateHouseDto {
    private Long type;
    private Long user;
    private String address;
}