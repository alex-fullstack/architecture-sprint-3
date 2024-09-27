package ru.yandex.practicum.controlpanel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.controlpanel.entity.DeviceType;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long> {
}
