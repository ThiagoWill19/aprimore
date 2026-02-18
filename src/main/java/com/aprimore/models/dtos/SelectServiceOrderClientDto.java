package com.aprimore.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectServiceOrderClientDto {

	@NotNull(message = "Cliente e obrigatorio")
	private UUID clientId;
}
