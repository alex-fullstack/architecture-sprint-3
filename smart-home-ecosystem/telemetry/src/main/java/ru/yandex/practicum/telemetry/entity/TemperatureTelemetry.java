package ru.yandex.practicum.telemetry.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "temperatures")
@Data
public class TemperatureTelemetry {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Sensor sensor;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}