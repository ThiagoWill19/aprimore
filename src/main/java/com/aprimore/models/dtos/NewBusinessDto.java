package com.aprimore.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class NewBusinessDto {

	@NotBlank(message = "O campo Razão social deve ser preenchido.")
	private String businessName;
	
	@NotBlank(message = "O campo Nome fantasia deve ser preenchido.")
	private String tradeName;
	
	@NotBlank(message = "O campo CNPJ deve ser preenchido.")
	private String cnpj;
	
	@NotBlank(message = "O campo email deve ser preenchido.")
	private String businessEmail;
	
	@NotBlank(message = "O campo telefone deve ser preenchido.")
	private String phone;
	
	@NotBlank(message = "O campo Nome do usuário deve ser preenchido.")
	private String username;
	
	@NotBlank(message = "O campo email do usuário deve ser preenchido.")
	private String email;	
}
