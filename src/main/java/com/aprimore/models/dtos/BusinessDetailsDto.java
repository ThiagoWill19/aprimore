package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.aprimore.models.enuns.AccountStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDetailsDto {

	private UUID id;
	
	@NotBlank(message = "Campo razão social não pode estar em branco!")
	private String name;
	
	@NotBlank(message = "Campo nome fantasia não pode estar em branco!")
	private String tradeName;
	
	@NotBlank(message = "Campo CNPJ não pode estar em branco!")
	private String cnpj;
	
	@NotBlank(message = "Campo email não pode estar em branco!")
	private String businessEmail;
	
	@NotBlank(message = "Campo telefone não pode estar em branco!")
	private String phone;
	
	private AccountStatus accountStatus;
	

	private LocalDate createdAt;
	

	private int quantityUser;
	

	private int quantityClients;
}
