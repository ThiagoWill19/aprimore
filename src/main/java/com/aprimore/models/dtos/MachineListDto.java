package com.aprimore.models.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineListDto {

	private UUID id;
	private String name;
	private String wave;
	private boolean active;
	private String type; // ROTARY | FLAT

}
