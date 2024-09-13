package ru.yandex.practicum.controlpanel.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.controlpanel.dto.CreateHouseDto;
import ru.yandex.practicum.controlpanel.dto.ReadHouseDto;
import ru.yandex.practicum.controlpanel.dto.ReadHouseTypeDto;
import ru.yandex.practicum.controlpanel.entity.House;
import ru.yandex.practicum.controlpanel.entity.HouseType;
import ru.yandex.practicum.controlpanel.repository.HouseRepository;
import ru.yandex.practicum.controlpanel.repository.HouseTypeRepository;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {
    private final HouseRepository houseRepository;
    private final HouseTypeRepository houseTypeRepository;
    
    @Override
    public ReadHouseDto get(Long id) {
        House house = houseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("House not found"));
        return convertToDto(house);
    }
 
 
    @Override
    public ReadHouseDto create(CreateHouseDto houseDto) {
        HouseType type = houseTypeRepository.findById(houseDto.getType())
                .orElseThrow(() -> new RuntimeException("House type not found"));
        House house = new House();
        house.setType(type);
        house.setUserId(houseDto.getUser());
        house.setAddress(houseDto.getAddress());
        houseRepository.save(house);
        return convertToDto(house);
    }

    private ReadHouseDto convertToDto(House house) {
        ReadHouseDto dto = new ReadHouseDto();
        dto.setAdress(house.getAddress());
        dto.setId(house.getId());
        dto.setUserId(house.getUserId());

        HouseType houseType = house.getType();
        ReadHouseTypeDto htdto= new ReadHouseTypeDto();
        htdto.setId(houseType.getId());
        htdto.setCode(houseType.getCode());
        dto.setType(htdto);
        return dto;
    }
}