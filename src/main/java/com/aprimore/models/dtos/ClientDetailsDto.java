package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class ClientDetailsDto {

	private UUID id;

	private String clientName;

	private String clientEmail;

	private String clientPhoneNumber;

	private boolean active;

	private LocalDate createdAt;

	private String standardOrderInstructions;
	
	private int qntMachines;
	
	private int qntServiceOrder;

}
