package ru.yandex.practicum.smarthome.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.event.SyncEvent;
import ru.yandex.practicum.smarthome.publisher.KafkaPublisher;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

@Service
@RequiredArgsConstructor
public class HeatingSystemServiceImpl implements HeatingSystemService {
    private final HeatingSystemRepository heatingSystemRepository;
    private final KafkaPublisher kafkaPublisher;
    
    @Override
    public HeatingSystemDto getHeatingSystem(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        return convertToDto(heatingSystem);
    }

    @Override
    public HeatingSystemDto updateHeatingSystem(Long id, HeatingSystemDto heatingSystemDto) {
        HeatingSystem existingHeatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        existingHeatingSystem.setOn(heatingSystemDto.isOn());
        existingHeatingSystem.setTargetTemperature(heatingSystemDto.getTargetTemperature());
        HeatingSystem updatedHeatingSystem = heatingSystemRepository.save(existingHeatingSystem);
        return convertToDto(updatedHeatingSystem);
    }

    @Override
    public void turnOn(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heatingSystem.setOn(true);
        heatingSystemRepository.save(heatingSystem);
    }

    @Override
    public void turnOff(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heatingSystem.setOn(false);
        heatingSystemRepository.save(heatingSystem);
    }

    @Override
    public void setTargetTemperature(Long id, double temperature) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heatingSystem.setTargetTemperature(temperature);
        heatingSystemRepository.save(heatingSystem);
    }

    @Override
    public Double getCurrentTemperature(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        return heatingSystem.getCurrentTemperature();
    }

    @Override
    public void sync() {
        List<HeatingSystem> heatingSystems = heatingSystemRepository.findAll();
        var dtos = heatingSystems.stream().map(value -> {
            var dto = new HeatingSystemDto();
            dto.setId(value.getId());
            dto.setOn(value.isOn());
            dto.setTargetTemperature(value.getTargetTemperature());
            dto.setCurrentTemperature(value.getCurrentTemperature());
            return dto;
        }).toList();
        var event = new SyncEvent();
        event.setHeatingSystemDtos(dtos);
        kafkaPublisher.sendSyncEvent(event);

    }

    private HeatingSystemDto convertToDto(HeatingSystem heatingSystem) {
        HeatingSystemDto dto = new HeatingSystemDto();
        dto.setId(heatingSystem.getId());
        dto.setOn(heatingSystem.isOn());
        dto.setTargetTemperature(heatingSystem.getTargetTemperature());
        return dto;
    }
}