package ru.yandex.practicum.smarthome.command;

import lombok.Data;

@Data
public class SetTargetTemperature {
    private double target;
}