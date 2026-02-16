package com.aprimore.models.dtos;

import java.util.UUID;

import com.aprimore.models.enuns.BladeType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BladeListDto {

    private UUID id;

    private String name;

    private String description;

    private String manufacturer;

    private String supplier;

    private BladeType bladeType;
}
