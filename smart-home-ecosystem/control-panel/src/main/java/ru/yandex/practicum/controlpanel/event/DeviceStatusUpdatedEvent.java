package ru.yandex.practicum.controlpanel.event;

import lombok.Data;

@Data
public class DeviceStatusUpdatedEvent {
    private Long id;
    private String code;
    private Long monolithId;
}