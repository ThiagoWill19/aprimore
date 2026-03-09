package com.aprimore.models.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardDto {

	private long openServiceOrders;
	private long closedServiceOrders;
	private long overdueServiceOrders;
	private List<String> alerts = new ArrayList<>();
	private List<DashboardPriorityServiceOrderDto> priorityServiceOrders = new ArrayList<>();
}
