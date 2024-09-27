package ru.yandex.practicum.controlpanel.entity;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "devices")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_type_id")
    private DeviceType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_status_id")
    private DeviceStatus status;

    @Column(nullable = true)
    private Long serialNumber;

    @Column(nullable = true)
    private Long monolithId;

    public Optional<House> getHouse() {
        return Optional.ofNullable(house);
    }
 
    public void setHouse(House house) {
        this.house = house;
    }
}