package ru.yandex.practicum.controlpanel.service;

import ru.yandex.practicum.controlpanel.command.Command;
import ru.yandex.practicum.controlpanel.dto.CreateDeviceDto;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceDto;
import ru.yandex.practicum.controlpanel.dto.UpdateDeviceDto;
import ru.yandex.practicum.controlpanel.event.SyncEvent;

public interface DeviceService {
    ReadDeviceDto get(Long id);
    ReadDeviceDto create(CreateDeviceDto deviceDto);
    ReadDeviceDto update(Long id, UpdateDeviceDto deviceDto);
    void toggle(Long id);
    void activate(Long id);
    void sync(SyncEvent event) throws RuntimeException;
    void execute(Long id, Command<?> command);
}