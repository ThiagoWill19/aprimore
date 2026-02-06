package com.aprimore.models.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressDto {

    private UUID clientId;

    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
}
