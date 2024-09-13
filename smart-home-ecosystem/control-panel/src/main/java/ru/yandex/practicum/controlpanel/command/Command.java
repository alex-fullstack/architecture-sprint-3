package ru.yandex.practicum.controlpanel.command;

import lombok.Data;

@Data
public class Command<T> {
    private String code;
    private T parameter; 
}