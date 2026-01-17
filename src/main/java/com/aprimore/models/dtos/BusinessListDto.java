package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessListDto {
	
	private UUID id;

	private String name;
	
	private String tradeName;
	
	private String cnpj;
	
	private LocalDate createdAt;
	
}
