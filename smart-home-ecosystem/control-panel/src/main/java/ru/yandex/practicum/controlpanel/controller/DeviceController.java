package ru.yandex.practicum.controlpanel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.controlpanel.command.Command;
import ru.yandex.practicum.controlpanel.dto.CreateDeviceDto;
import ru.yandex.practicum.controlpanel.dto.ReadDeviceDto;
import ru.yandex.practicum.controlpanel.dto.UpdateDeviceDto;
import ru.yandex.practicum.controlpanel.service.DeviceService;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @GetMapping("/{id}")
    public ResponseEntity<ReadDeviceDto> getDevice(@PathVariable("id") Long id) {
        logger.info("Fetching device with id {}", id);
        return ResponseEntity.ok(deviceService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadDeviceDto> update(@PathVariable("id") Long id, @RequestBody UpdateDeviceDto updateDeviceDto) {
        logger.info("Updating device");
        return ResponseEntity.ok(deviceService.update(id, updateDeviceDto));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleStatus(@PathVariable("id") Long id) {
        logger.info("Changing device status with id {}", id);
        deviceService.toggle(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/create")
    public ResponseEntity<ReadDeviceDto> create(@RequestBody CreateDeviceDto createDeviceDto) {
        logger.info("Creating new device");
        return ResponseEntity.ok(deviceService.create(createDeviceDto));
    }

    @PostMapping("/{id}/command")
    public ResponseEntity<Void> command(@PathVariable("id") Long id, @RequestBody Command<?> command) {
        deviceService.execute(id, command);
        return ResponseEntity.noContent().build();
    }

}
