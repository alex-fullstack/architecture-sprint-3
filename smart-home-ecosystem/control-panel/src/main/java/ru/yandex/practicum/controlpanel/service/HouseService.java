package ru.yandex.practicum.controlpanel.service;

import ru.yandex.practicum.controlpanel.dto.CreateHouseDto;
import ru.yandex.practicum.controlpanel.dto.ReadHouseDto;

public interface HouseService {
    ReadHouseDto get(Long id);
    ReadHouseDto create(CreateHouseDto houseDto);
}