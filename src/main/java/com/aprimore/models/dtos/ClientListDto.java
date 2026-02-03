package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientListDto {

	private UUID id;
	private String clientName;
	private boolean active;
	private LocalDate createdAt;

}
