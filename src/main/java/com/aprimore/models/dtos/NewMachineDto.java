package com.aprimore.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewMachineDto {

	private String name;
	private String type;
	private String wave;
	private String description;
	private String observations;

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
