package ru.yandex.practicum.telemetry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.telemetry.entity.SensorType;

@Repository
public interface SensorTypeRepository extends JpaRepository<SensorType, Long> {
}
