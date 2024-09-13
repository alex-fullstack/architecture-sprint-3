package ru.yandex.practicum.telemetry.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.telemetry.dto.CreateSensorDto;
import ru.yandex.practicum.telemetry.dto.ReadSensorDto;
import ru.yandex.practicum.telemetry.dto.ReadSensorTypeDto;
import ru.yandex.practicum.telemetry.dto.ReadTelemetryDto;
import ru.yandex.practicum.telemetry.dto.ReadTemperatureTelemetryDto;
import ru.yandex.practicum.telemetry.dto.ReadUnitDto;
import ru.yandex.practicum.telemetry.dto.UpdateSensorDto;
import ru.yandex.practicum.telemetry.entity.Sensor;
import ru.yandex.practicum.telemetry.entity.SensorType;
import ru.yandex.practicum.telemetry.entity.TemperatureTelemetry;
import ru.yandex.practicum.telemetry.entity.Unit;
import ru.yandex.practicum.telemetry.event.TemperatureUpdatedEvent;
import ru.yandex.practicum.telemetry.publisher.KafkaPublisher;
import ru.yandex.practicum.telemetry.repository.SensorRepository;
import ru.yandex.practicum.telemetry.repository.SensorTypeRepository;
import ru.yandex.practicum.telemetry.repository.TemperatureTelemetryRepository;
import ru.yandex.practicum.telemetry.repository.UnitRepository;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;
    private final SensorTypeRepository sensorTypeRepository;
    private final TemperatureTelemetryRepository temperatureTelemetryRepository;
    private final UnitRepository unitRepository;
    private final KafkaPublisher kafkaPublisher;

    @Override
    public void bulkCreate(Long deviceId, List<CreateSensorDto> sensoreDtos) throws RuntimeException {
        List<Sensor> sensors = new ArrayList<>();
        for (CreateSensorDto dto : sensoreDtos) {        
            Sensor sensor = new Sensor();
            SensorType exampleType = new SensorType();
            exampleType.setCode(dto.getType());
            Example<SensorType> example = Example.of(exampleType);
            Optional<SensorType> type = sensorTypeRepository.findOne(example);
            type.ifPresentOrElse(value -> {
                sensor.setType(value);
            }, () -> new RuntimeException("Type not found"));
            Unit exampleUnit = new Unit();
            exampleUnit.setCode(dto.getUnit());
            Example<Unit> exampleU = Example.of(exampleUnit);
            Optional<Unit> unit = unitRepository.findOne(exampleU);
            unit.ifPresentOrElse(value -> {
                sensor.setUnit(value);
            }, () -> new RuntimeException("Unit not found"));
            sensor.setDeviceId(deviceId);
            sensor.setSerialNumber(dto.getSerialNumber());
            sensors.add(sensor);
        }
        try {
            sensorRepository.saveAll(sensors);
            kafkaPublisher.sendBulkCreatedEvent(deviceId.toString());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e.getMessage());
        }
        
    }

    @Override
    public void bulkCreateRollback(String deviceId) {
        kafkaPublisher.sendBulkCreateFailedEvent(deviceId);
    }

    @Transactional
    @Override
    public void bulkUpdate(Long deviceId, List<UpdateSensorDto> sensorDtos) throws RuntimeException {
        Sensor exampleSensor = new Sensor();
        exampleSensor.setDeviceId(deviceId);
        Example<Sensor> example = Example.of(exampleSensor);
        List<Sensor> sensors = sensorRepository.findAll(example);
        for (UpdateSensorDto dto : sensorDtos) {
            var sensor = sensors.stream().filter(s -> Objects.equals(s.getId(), dto.getId())).toList().get(0);
            sensor.setSerialNumber(dto.getSerialNumber());
        }
        try {
            sensorRepository.saveAll(sensors);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void bulkUpdateRollback(String deviceId) {
        kafkaPublisher.sendBulkUpdateFailedEvent(deviceId);
    }

    @Override
    @Transactional
    public void updateCurrentTemperature(TemperatureUpdatedEvent event) {
        Sensor exampleSensor = new Sensor();
        exampleSensor.setSerialNumber(event.getSensorSerialNumber());
        Example<Sensor> example = Example.of(exampleSensor);
        Optional<Sensor> sensor = sensorRepository.findOne(example);
        sensor.ifPresentOrElse(value -> {
            if (!value.getType().getCode().equals("temperature")) {
                throw new RuntimeException("Wrong sensor type");
            }
            TemperatureTelemetry tt = new TemperatureTelemetry();
            tt.setSensor(value);
            tt.setValue(event.getValue());
            tt.setTimestamp(event.getTimestamp());
            temperatureTelemetryRepository.save(tt);
        }, () -> new RuntimeException("Sensor not found"));
    }

    @Override
    public ReadTelemetryDto<?> getTelemetry(Long id) {
        Sensor sensor = sensorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sensor not found"));
        var sensorType = sensor.getType();
        var sensorUnit = sensor.getUnit();
        var sensorDto = new ReadSensorDto();
        sensorDto.setDeviceId(sensor.getDeviceId());
        sensorDto.setId(sensor.getId());
        sensorDto.setSerialNumber(sensor.getSerialNumber());
        var sensorTypeDto = new ReadSensorTypeDto();
        sensorTypeDto.setId(id);
        var sensorTypeCode = sensorType.getCode();
        sensorTypeDto.setCode(sensorTypeCode);
        sensorDto.setType(sensorTypeDto);
        var sensorUnitDto = new ReadUnitDto();
        sensorUnitDto.setId(sensorUnit.getId());
        sensorUnitDto.setCode(sensorUnit.getCode());
        sensorDto.setUnit(sensorUnitDto);
        switch (sensorTypeCode) {
            case "temperature" -> {
                var telemetry = temperatureTelemetryRepository.findAll();
                var dto = new ReadTelemetryDto<ReadTemperatureTelemetryDto>();
                dto.setSensor(sensorDto);
                dto.setHistory(telemetry.stream().map(value -> {
                    var item = new ReadTemperatureTelemetryDto();
                    item.setId(value.getId());
                    item.setValue(value.getValue());
                    item.setTimestamp(value.getTimestamp());
                    return item;
                }).toList());
                return dto;
            }
            default -> throw new RuntimeException("Wrong sensor type!");
        }
    }
}