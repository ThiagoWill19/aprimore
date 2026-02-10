package com.aprimore.models;

import com.aprimore.models.enuns.BladeType;

import jakarta.persistence.Entity;
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

	private BladeType bladeType;
	
	
}
