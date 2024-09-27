package ru.yandex.practicum.smarthome.command;

import lombok.Data;

@Data
public class Command<T> {
    private String code;
    private T parameter; 
}