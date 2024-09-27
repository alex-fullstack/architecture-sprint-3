package ru.yandex.practicum.controlpanel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.controlpanel.entity.DeviceStatus;

@Repository
public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {
}
