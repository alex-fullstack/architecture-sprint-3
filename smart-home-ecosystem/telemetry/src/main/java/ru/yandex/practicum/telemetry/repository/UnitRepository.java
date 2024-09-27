package ru.yandex.practicum.telemetry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.telemetry.entity.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
}
