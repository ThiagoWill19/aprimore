package com.aprimore.models.mappers;

import org.springframework.stereotype.Component;

import com.aprimore.models.Address;
import com.aprimore.models.dtos.UpdateAddressDto;

@Component
public class AddressMapper {

    public void updateAddressFromDto(UpdateAddressDto dto, Address address) {

        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setNeighborhood(dto.getNeighborhood());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
    }
}
