package com.aprimore.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewClientDto {

	@NotBlank(message = "O nome da empresa é obrigatório")
	private String clientName;
	
	@NotBlank(message = "O email da empresa é obrigatório")
	private String clientEmail;
	
	private String clientPhoneNumber;
}
