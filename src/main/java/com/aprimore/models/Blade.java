package com.aprimore.models;

import com.aprimore.models.enuns.BladeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Blade extends Item {

	@Enumerated(EnumType.STRING)
	private BladeType bladeType;

	private int numberOfTeeth;
	private int espessure;
	private double height;
	private String cutType; //Serrilhado / Liso

	
	
}
