package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.aprimore.models.enuns.ServiceOrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceOrderListDto {

	private Long id;
	private String workName;
	private String reference;
	private LocalDate deliveryDate;
	private LocalDateTime createdAt;
	private ServiceOrderStatus status;
}
