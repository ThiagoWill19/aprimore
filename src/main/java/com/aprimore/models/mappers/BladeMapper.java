package com.aprimore.models.mappers;

import org.springframework.stereotype.Component;

import com.aprimore.models.Blade;
import com.aprimore.models.Business;
import com.aprimore.models.dtos.BladeListDto;
import com.aprimore.models.dtos.NewBladeDto;

@Component
public class BladeMapper {

    public Blade mapToBlade(NewBladeDto dto, Business business) {

        Blade blade = new Blade();
        blade.setName(dto.getName().trim());
        blade.setDescription(dto.getDescription());
        blade.setManufacturer(dto.getManufacturer());
        blade.setSupplier(dto.getSupplier());
        blade.setBladeType(dto.getBladeType());
        blade.setBusiness(business);

        return blade;
    }

    public BladeListDto mapToBladeListDto(Blade blade) {

        BladeListDto dto = new BladeListDto();
        dto.setId(blade.getId());
        dto.setName(blade.getName());
        dto.setDescription(blade.getDescription());
        dto.setManufacturer(blade.getManufacturer());
        dto.setSupplier(blade.getSupplier());
        dto.setBladeType(blade.getBladeType());

        return dto;
    }
}
