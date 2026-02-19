package com.aprimore.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MachineDetailsDto {

	private UUID id;
	private UUID clientId;
	private String clientName;
	private String type; // ROTARY | FLAT

	@NotBlank(message = "Nome é obrigatório")
	private String name;

	@NotBlank(message = "Tipo de onda é obrigatório")
	private String wave;

	private String description;
	private String observations;
	private boolean active;

	// Rotary
	private String diameter;
	private String centerLine;
	private String totalLengthCylinder;
	private String reduction;
	private String distanceBetweenHolesInStraightLine;
	private String distanceBetweenHolesInCurvedDirection;
	private boolean hasMeshPattern;

	// Flat
	private String maxSheetDimension;
}
