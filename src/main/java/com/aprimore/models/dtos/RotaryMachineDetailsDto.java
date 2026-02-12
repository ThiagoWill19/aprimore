package com.aprimore.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RotaryMachineDetailsDto {

	private UUID id;
	
	private String clientName;

	private String name;

	private String description;

	private String observations;

	private String wave;

	private boolean active;

	private String diameter;

	private String centerLine;

	private String totalLengthCylinder;

	private String reduction;

	private String drillingInformation;

}
