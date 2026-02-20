package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceOrderDetailsDto {

	private Long id;
	private UUID clientId;

	@NotBlank(message = "Nome do trabalho e obrigatorio")
	private String workName;

	private String reference;
	private LocalDate entryDate;
	private LocalDate deliveryDate;
	private LocalDateTime createdAt;
	private String internalMeasure;

	@NotNull(message = "Maquina e obrigatoria")
	private UUID machineId;

	@NotBlank(message = "Tipo de onda e obrigatorio")
	private String typeOfWave;

	private String arrangement;
	private String servicesToBePerformed;
	private String obs;
	private List<UUID> bladeIds = new ArrayList<>();
}
