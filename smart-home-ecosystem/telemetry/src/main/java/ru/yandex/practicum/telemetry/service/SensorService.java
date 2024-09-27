package ru.yandex.practicum.telemetry.service;


import java.util.List;

import ru.yandex.practicum.telemetry.dto.CreateSensorDto;
import ru.yandex.practicum.telemetry.dto.ReadTelemetryDto;
import ru.yandex.practicum.telemetry.dto.UpdateSensorDto;
import ru.yandex.practicum.telemetry.event.TemperatureUpdatedEvent;

public interface SensorService {
    void bulkCreate(Long deviceId, List<CreateSensorDto> sensorDtos) throws RuntimeException;
    void bulkCreateRollback(String deviceId);
    void bulkUpdate(Long deviceId, List<UpdateSensorDto> sensorDtos) throws RuntimeException;
    void bulkUpdateRollback(String deviceId);
    void updateCurrentTemperature(TemperatureUpdatedEvent event);
    ReadTelemetryDto<?> getTelemetry(Long id);
}