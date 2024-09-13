package ru.yandex.practicum.controlpanel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.controlpanel.entity.House;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
}
