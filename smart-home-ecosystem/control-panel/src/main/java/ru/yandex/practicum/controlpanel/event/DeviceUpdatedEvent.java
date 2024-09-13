package ru.yandex.practicum.controlpanel.event;

import java.util.List;

import lombok.Data;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceDto;
import ru.yandex.practicum.controlpanel.dto.UpdateSensorDto;

@Data
public class DeviceUpdatedEvent {
    private ReadDeviceDto device;
    private List<UpdateSensorDto> sensors;
}