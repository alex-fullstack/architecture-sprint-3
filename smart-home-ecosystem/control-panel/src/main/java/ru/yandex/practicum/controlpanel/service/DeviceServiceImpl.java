package ru.yandex.practicum.controlpanel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.controlpanel.command.Command;
import ru.yandex.practicum.controlpanel.command.SetTargetTemperature;
import ru.yandex.practicum.controlpanel.dto.CreateDeviceDto;
import ru.yandex.practicum.controlpanel.dto.CreateSensorDto;
import ru.yandex.practicum.controlpanel.dto.HeatingSystemDto;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceDto;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceStatusDto;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceTypeDto;
import ru.yandex.practicum.controlpanel.dto.ReadHouseDto;
import ru.yandex.practicum.controlpanel.dto.ReadHouseTypeDto;
import ru.yandex.practicum.controlpanel.dto.UpdateDeviceDto;
import ru.yandex.practicum.controlpanel.entity.Device;
import ru.yandex.practicum.controlpanel.entity.DeviceStatus;
import ru.yandex.practicum.controlpanel.entity.DeviceType;
import ru.yandex.practicum.controlpanel.entity.House;
import ru.yandex.practicum.controlpanel.entity.HouseType;
import ru.yandex.practicum.controlpanel.entity.TemperatureModule;
import ru.yandex.practicum.controlpanel.event.DeviceCommandExecutedEvent;
import ru.yandex.practicum.controlpanel.event.DeviceCreatedEvent;
import ru.yandex.practicum.controlpanel.event.DeviceStatusUpdatedEvent;
import ru.yandex.practicum.controlpanel.event.DeviceUpdatedEvent;
import ru.yandex.practicum.controlpanel.event.SyncEvent;
import ru.yandex.practicum.controlpanel.publisher.KafkaPublisher;
import ru.yandex.practicum.controlpanel.repository.DeviceRepository;
import ru.yandex.practicum.controlpanel.repository.DeviceStatusRepository;
import ru.yandex.practicum.controlpanel.repository.DeviceTypeRepository;
import ru.yandex.practicum.controlpanel.repository.HouseRepository;
import ru.yandex.practicum.controlpanel.repository.TemperatureModuleRepository;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final HouseRepository houseRepository;
    private final DeviceStatusRepository deviceStatusRepository;
    private final TemperatureModuleRepository temperatureModuleRepository;
    private final KafkaPublisher kafkaPublisher;

    @Override
    public ReadDeviceDto get(Long id) {
        Device device = deviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        return convertToDto(device);
    }
    
    @Override
    public ReadDeviceDto create(CreateDeviceDto deviceDto) {
        Device device = new Device();
        DeviceType type = deviceTypeRepository.findById(deviceDto.getType())
                .orElseThrow(() -> new RuntimeException("Device type not found"));
        House house = houseRepository.findById(deviceDto.getHouse())
                .orElseThrow(() -> new RuntimeException("House not found"));
        DeviceStatus exampleStatus = new DeviceStatus();
        exampleStatus.setCode("new");
        Example<DeviceStatus> example = Example.of(exampleStatus);
        Optional<DeviceStatus> status = deviceStatusRepository.findOne(example);
        status.ifPresentOrElse(value -> {
            device.setStatus(value);
        }, () -> new RuntimeException("Status not found"));
        device.setType(type);
        device.setSerialNumber(deviceDto.getSerialNumber());
        device.setHouse(house);
        deviceRepository.save(device);
        
        var dto =  convertToDto(device);
        var event = new DeviceCreatedEvent();
        event.setDevice(dto);
        event.setSensors(deviceDto.getSensors());
        kafkaPublisher.sendCreatedEvent(event);
        return dto;
    }

    @Transactional
    @Override
    public ReadDeviceDto update(Long id, UpdateDeviceDto deviceDto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        House house = houseRepository.findById(deviceDto.getHouse())
                .orElseThrow(() -> new RuntimeException("House not found"));
        device.setHouse(house);
        device.setSerialNumber(deviceDto.getSerialNumber());
        deviceRepository.save(device);
        var dto = convertToDto(device);
        var event = new DeviceUpdatedEvent();
        event.setDevice(dto);
        event.setSensors(deviceDto.getSensors());
        kafkaPublisher.sendUpdatedEvent(event);
        return dto;
    }

    @Override
    public void toggle(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        List<DeviceStatus> statuses = deviceStatusRepository.findAll();
        DeviceStatus status = device.getStatus();
        switch (status.getCode()) {
            case "on" -> {
                statuses.forEach(value -> {
                    if (value.getCode().equals("off")) {
                        device.setStatus(value);
                    }
                });
            }
            case "off" -> {
                statuses.forEach(value -> {
                    if (value.getCode().equals("on")) {
                        device.setStatus(value);
                    }
                });
            }
            default -> throw new RuntimeException("Wrong status");
        }
        deviceRepository.save(device);
        var event = new DeviceStatusUpdatedEvent();
        status = device.getStatus();
        event.setCode(status.getCode());
        event.setId(device.getId());
        event.setMonolithId(device.getMonolithId());
        kafkaPublisher.sendStatusUpdatedEvent(event);
    }

    @Transactional
    @Override
    public void activate(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        List<DeviceStatus> statuses = deviceStatusRepository.findAll();
        DeviceStatus status = device.getStatus();
        switch (status.getCode()) {
            case "new" -> {
                statuses.forEach(value -> {
                    if (value.getCode().equals("off")) {
                        device.setStatus(value);
                    }
                });
            }
            default -> throw new RuntimeException("Wrong status");
        }
        deviceRepository.save(device);
        var event = new DeviceStatusUpdatedEvent();
        status = device.getStatus();
        event.setCode(status.getCode());
        event.setId(device.getId());
        event.setMonolithId(device.getMonolithId());
        kafkaPublisher.sendStatusUpdatedEvent(event);
    }


    @Transactional
    @Override
    public void sync(SyncEvent syncEvent) throws RuntimeException {
        for (HeatingSystemDto item: syncEvent.getHeatingSystemDtos())
        {
            Device device = new Device();
            DeviceType exampleType = new DeviceType();
            exampleType.setCode("heating");
            Example<DeviceType> example = Example.of(exampleType);
            Optional<DeviceType> type = deviceTypeRepository.findOne(example);
            type.ifPresentOrElse(value -> {
                device.setType(value);
            }, () -> new RuntimeException("Status not found"));
            DeviceStatus exampleStatus = new DeviceStatus();
            if (item.isOn()) {
                exampleStatus.setCode("on");
            } else {
                exampleStatus.setCode("off");
            }
        
            Example<DeviceStatus> exampleSt = Example.of(exampleStatus);
            Optional<DeviceStatus> status = deviceStatusRepository.findOne(exampleSt);
            status.ifPresentOrElse(value -> {
                device.setStatus(value);
            }, () -> new RuntimeException("Status not found"));
            device.setMonolithId(item.getId());
            try {
                var saved = deviceRepository.save(device);
                TemperatureModule tm = new TemperatureModule();
                tm.setTarget(item.getTargetTemperature());
                tm.setDevice(saved);
                temperatureModuleRepository.save(tm);
                var dto =  convertToDto(saved);
                var event = new DeviceCreatedEvent();
                event.setDevice(dto);
                List<CreateSensorDto> sensors = new ArrayList<>();
                var temperatureSensor = new CreateSensorDto();
                temperatureSensor.setType("temperature");
                temperatureSensor.setUnit("celsius");
                sensors.add(temperatureSensor);
                event.setSensors(sensors);
                kafkaPublisher.sendCreatedEvent(event);
            } catch (DataIntegrityViolationException e) {
                var msg = e.getMessage();
                if (!msg.contains("duplicate key value violates unique constraint")) {
                    throw new RuntimeException(msg);
                }                
            }
            
        }
    }

    @Override
    public void execute(Long id, Command<?> command) {
        switch (command.getCode()) {
            case "set-temperature" -> {
                log.info(command.toString());
                var mapper = new ObjectMapper()
                    .enable(SerializationFeature.INDENT_OUTPUT);
                try {
                    SetTargetTemperature cmdParameter = mapper.readValue(mapper.writeValueAsString(command.getParameter()), SetTargetTemperature.class);
                    Device device = deviceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Device not found"));
                    if (!device.getType().getCode().equals("heating")) {
                            throw new RuntimeException("Wrong Device type!");
                    }
                    Optional<TemperatureModule> currentTm = temperatureModuleRepository.findById(id);
                    currentTm.ifPresentOrElse(
                        (TemperatureModule value) -> {
                            value.setTarget(cmdParameter.getTarget());
                            temperatureModuleRepository.save(value);
                        },
                        () -> {
                            TemperatureModule tm = new TemperatureModule();
                            tm.setTarget(cmdParameter.getTarget());
                            tm.setDevice(device);
                            temperatureModuleRepository.save(tm);
                        }
                    );
                    
                    var event = new DeviceCommandExecutedEvent();
                    event.setId(id);
                    event.setMonolithId(device.getMonolithId());
                    var cmd = new Command<SetTargetTemperature>();
                    cmd.setCode("set-temperature");
                    cmd.setParameter(cmdParameter);
                    event.setCommand(cmd);
                    kafkaPublisher.sendCommandExecutedEvent(event);
                } catch (JsonProcessingException e) {
                    log.info("Can't execute [{}]", e.getMessage());
                }
                
            }
            default -> throw new RuntimeException("Wrong command code!");
        }
    }

    private ReadDeviceDto convertToDto(Device device) {
        ReadDeviceDto dto = new ReadDeviceDto();        
        dto.setId(device.getId());
        dto.setSerialNumber(device.getSerialNumber());

        DeviceStatus deviceStatus = device.getStatus();
        ReadDeviceStatusDto sdto = new ReadDeviceStatusDto();
        sdto.setId(deviceStatus.getId());
        sdto.setCode(deviceStatus.getCode());
        dto.setStatus(sdto);
 
        DeviceType deviceType = device.getType();
        ReadDeviceTypeDto tdto = new ReadDeviceTypeDto();
        tdto.setId(deviceType.getId());
        tdto.setCode(deviceType.getCode());
        dto.setType(tdto);

        Optional<House> house = device.getHouse();
        house.ifPresent(value -> {
            ReadHouseDto hdto = new ReadHouseDto();
            hdto.setAdress(value.getAddress());
            hdto.setId(value.getId());
            hdto.setUserId(value.getUserId());

            HouseType houseType = value.getType();
            ReadHouseTypeDto htdto= new ReadHouseTypeDto();
            htdto.setId(houseType.getId());
            htdto.setCode(houseType.getCode());
            hdto.setType(htdto);
            dto.setHouse(hdto);
        });
        dto.setMonolithId(device.getMonolithId());
        return dto;
    }
}