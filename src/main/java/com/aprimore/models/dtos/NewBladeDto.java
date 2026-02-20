package com.aprimore.models.dtos;

import com.aprimore.models.enuns.BladeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewBladeDto {

    private String description;

    private String manufacturer;

    private String supplier;

    private int espessure;

    private double height;

    private String cutType; //Serrilhado / Liso

    @NotNull(message = "O tipo da lâmina é obrigatório")
    private BladeType bladeType;
}
