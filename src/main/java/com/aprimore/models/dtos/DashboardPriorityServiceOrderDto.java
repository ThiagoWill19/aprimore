package com.aprimore.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardPriorityServiceOrderDto {

	private Long id;
	private UUID clientId;
	private String clientName;
	private String workName;
	private LocalDate entryDate;
	private LocalDate deliveryDate;
	private Integer pcpSequence;
	private boolean overdue;
}
