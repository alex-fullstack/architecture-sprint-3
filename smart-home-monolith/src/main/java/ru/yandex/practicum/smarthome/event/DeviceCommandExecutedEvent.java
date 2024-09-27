package ru.yandex.practicum.smarthome.event;

import lombok.Data;
import ru.yandex.practicum.smarthome.command.Command;

@Data
public class DeviceCommandExecutedEvent {
    private Long id;
    private Command<?> command;
    private Long monolithId;
}