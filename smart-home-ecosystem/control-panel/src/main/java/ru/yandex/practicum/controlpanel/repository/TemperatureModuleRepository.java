package ru.yandex.practicum.controlpanel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.controlpanel.entity.TemperatureModule;

@Repository
public interface TemperatureModuleRepository extends JpaRepository<TemperatureModule, Long> {
}
