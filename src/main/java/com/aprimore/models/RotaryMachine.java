package com.aprimore.models;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RotaryMachine extends Machine {

	private String diameter;
	
	private String centerLine;
	
	private String totalLengthCylinder;
	
	private String reduction;

	private String distanceBetweenHolesInStraightLine;

	private String distanceBetweenHolesInCurvedDirection;

	private boolean hasMeshPattern;
	
}
