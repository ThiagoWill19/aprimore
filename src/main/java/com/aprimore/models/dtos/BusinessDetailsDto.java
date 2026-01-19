package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.aprimore.models.enuns.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDetailsDto {

	private UUID id;
	
	private String name;
	
	private String tradeName;
	
	private String cnpj;
	
	private String businessEmail;
	
	private String phone;
	
	private AccountStatus accountStatus;
	
	private LocalDate createdAt;
	
	private int quantityUser;
	
	private int quantityClients;
}
