package ru.yandex.practicum.telemetry.event;

import java.util.List;

import lombok.Data;
import ru.yandex.practicum.telemetry.dto.DeviceDto;
import ru.yandex.practicum.telemetry.dto.UpdateSensorDto;


@Data
public class UpdatedDeviceEvent {
    private DeviceDto device;
    private List<UpdateSensorDto> sensors;
}