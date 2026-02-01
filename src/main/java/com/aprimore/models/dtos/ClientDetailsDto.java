package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientDetailsDto {

	private UUID id;

	@NotBlank(message = "Nome é obrigatório")
	private String clientName;

	@NotBlank(message = "Email é obrigatório")
	private String clientEmail;

	@NotBlank(message = "Telefone é obrigatório")
	private String clientPhoneNumber;

	private boolean active;

	private LocalDate createdAt;

	private String standardOrderInstructions;
	
	private int qntMachines;
	
	private int qntServiceOrder;

}
