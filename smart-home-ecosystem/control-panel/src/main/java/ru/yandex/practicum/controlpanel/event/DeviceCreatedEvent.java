package ru.yandex.practicum.controlpanel.event;

import java.util.List;

import lombok.Data;
import ru.yandex.practicum.controlpanel.dto.CreateSensorDto;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceDto;

@Data
public class DeviceCreatedEvent {
    private ReadDeviceDto device;
    private List<CreateSensorDto> sensors;
}