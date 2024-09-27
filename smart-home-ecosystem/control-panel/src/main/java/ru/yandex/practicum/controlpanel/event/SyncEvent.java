package ru.yandex.practicum.controlpanel.event;

import java.util.List;

import lombok.Data;
import ru.yandex.practicum.controlpanel.dto.HeatingSystemDto;

@Data
public class SyncEvent {
    private List<HeatingSystemDto> heatingSystemDtos;
}