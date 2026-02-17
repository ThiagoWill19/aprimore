package com.aprimore.models.dtos;

import com.aprimore.models.enuns.BladeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewBladeDto {

    @NotBlank(message = "O nome da lâmina é obrigatório")
    private String name;

    private String description;

    private String manufacturer;

    private String supplier;

    @NotNull(message = "O tipo da lâmina é obrigatório")
    private BladeType bladeType;
}
