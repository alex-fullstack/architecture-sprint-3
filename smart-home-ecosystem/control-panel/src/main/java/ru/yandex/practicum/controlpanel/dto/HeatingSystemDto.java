package ru.yandex.practicum.controlpanel.dto;

import lombok.Data;

@Data
public class HeatingSystemDto {
    private Long id;
    private boolean isOn;
    private double targetTemperature;
    private double currentTemperature;
}