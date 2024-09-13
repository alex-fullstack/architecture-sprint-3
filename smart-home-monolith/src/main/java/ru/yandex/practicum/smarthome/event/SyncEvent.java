package ru.yandex.practicum.smarthome.event;

import java.util.List;

import lombok.Data;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;

@Data
public class SyncEvent {
    private List<HeatingSystemDto> heatingSystemDtos;
}