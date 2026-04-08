package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewServiceOrderDto {

	@NotBlank(message = "Nome do trabalho e obrigatório")
	private String workName;

	private String reference;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate entryDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate deliveryDate;

	private String internalMeasure;

	@NotNull(message = "Máquina e obrigatória")
	private UUID machineId;

	@NotBlank(message = "Tipo de onda e obrigatório")
	private String typeOfWave;

	private String arrangement;

	private String servicesToBePerformed;

	private String obs;

	private List<UUID> bladeIds = new ArrayList<>();
}
