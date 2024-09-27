package ru.yandex.practicum.telemetry.event;

import java.util.List;

import lombok.Data;
import ru.yandex.practicum.telemetry.dto.CreateSensorDto;
import ru.yandex.practicum.telemetry.dto.DeviceDto;

@Data
public class CreatedDeviceEvent {
    private DeviceDto device;
    private List<CreateSensorDto> sensors;
}