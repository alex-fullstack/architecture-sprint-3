package ru.yandex.practicum.controlpanel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.controlpanel.dto.CreateHouseDto;
import ru.yandex.practicum.controlpanel.dto.ReadHouseDto;
import ru.yandex.practicum.controlpanel.service.HouseService;

@RestController
@RequestMapping("/api/house")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService houseService;
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @GetMapping("/{id}")
    public ResponseEntity<ReadHouseDto> getDevice(@PathVariable("id") Long id) {
        logger.info("Fetching device with id {}", id);
        return ResponseEntity.ok(houseService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ReadHouseDto> create(@RequestBody CreateHouseDto createHouseDto) {
        logger.info("Creating new house");
        return ResponseEntity.ok(houseService.create(createHouseDto));
    }
}
