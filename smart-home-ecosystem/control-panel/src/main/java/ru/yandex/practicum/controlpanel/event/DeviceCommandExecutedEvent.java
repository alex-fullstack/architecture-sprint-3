package ru.yandex.practicum.controlpanel.event;

import lombok.Data;
import ru.yandex.practicum.controlpanel.command.Command;

@Data
public class DeviceCommandExecutedEvent {
    private Long id;
    private Command<?> command;
    private Long monolithId;
}