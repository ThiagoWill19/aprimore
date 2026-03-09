package com.aprimore.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aprimore.models.ServiceOrder;
import com.aprimore.models.User;
import com.aprimore.models.dtos.DashboardDto;
import com.aprimore.models.dtos.DashboardPriorityServiceOrderDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.ServiceOrderStatus;
import com.aprimore.repositories.ServiceOrderRepository;

@Service
public class DashboardService {

	@Autowired
	private ServiceOrderRepository serviceOrderRepository;

	public DashboardDto loadDashboard(User user) {

		validateBusinessIsActive(user);

		UUID businessId = user.getBusiness().getId();
		LocalDate today = LocalDate.now();

		DashboardDto dashboard = new DashboardDto();
		dashboard.setOpenServiceOrders(
				serviceOrderRepository.countByClientBusinessIdAndStatus(businessId, ServiceOrderStatus.OPEN));
		dashboard.setClosedServiceOrders(
				serviceOrderRepository.countByClientBusinessIdAndStatus(businessId, ServiceOrderStatus.CLOSED));
		dashboard.setOverdueServiceOrders(
				serviceOrderRepository.countByClientBusinessIdAndStatusAndDeliveryDateBefore(
						businessId,
						ServiceOrderStatus.OPEN,
						today));
		dashboard.setPriorityServiceOrders(loadPriorityServiceOrders(businessId, today));
		dashboard.setAlerts(buildAlerts(dashboard));

		return dashboard;
	}

	private List<DashboardPriorityServiceOrderDto> loadPriorityServiceOrders(UUID businessId, LocalDate today) {

		List<ServiceOrder> priorityOrders = serviceOrderRepository
				.findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN);

		List<DashboardPriorityServiceOrderDto> items = new ArrayList<>();
		for (ServiceOrder serviceOrder : priorityOrders) {
			DashboardPriorityServiceOrderDto item = new DashboardPriorityServiceOrderDto();
			item.setId(serviceOrder.getId());
			item.setClientId(serviceOrder.getClient().getId());
			item.setClientName(serviceOrder.getClient().getClientName());
			item.setWorkName(serviceOrder.getWorkName());
			item.setEntryDate(serviceOrder.getEntryDate());
			item.setDeliveryDate(serviceOrder.getDeliveryDate());
			item.setOverdue(serviceOrder.getDeliveryDate() != null && serviceOrder.getDeliveryDate().isBefore(today));
			item.setPcpSequence(serviceOrder.getPcpSequence());
			items.add(item);
		}

		return items;
	}

	@Transactional
	public void updatePcpSequence(List<Long> orderedIds, User user) {

		validateBusinessIsActive(user);

		if (orderedIds == null || orderedIds.isEmpty()) {
			return;
		}

		UUID businessId = user.getBusiness().getId();
		List<Long> uniqueIds = new ArrayList<>(new LinkedHashSet<>(orderedIds));
		List<ServiceOrder> requestedOrders = serviceOrderRepository.findByBusinessAndStatusAndIdIn(
				businessId,
				ServiceOrderStatus.OPEN,
				uniqueIds);

		if (requestedOrders.size() != uniqueIds.size()) {
			throw new AccessDeniedException("Sequencia invalida para a empresa logada.");
		}

		Map<Long, ServiceOrder> orderById = requestedOrders.stream()
				.collect(Collectors.toMap(ServiceOrder::getId, serviceOrder -> serviceOrder));

		int sequence = 1;
		for (Long serviceOrderId : uniqueIds) {
			ServiceOrder serviceOrder = orderById.get(serviceOrderId);
			serviceOrder.setPcpSequence(sequence++);
		}

		List<ServiceOrder> allOpenOrders = serviceOrderRepository.findPriorityByBusinessAndStatus(
				businessId,
				ServiceOrderStatus.OPEN);
		for (ServiceOrder serviceOrder : allOpenOrders) {
			if (!orderById.containsKey(serviceOrder.getId())) {
				serviceOrder.setPcpSequence(sequence++);
			}
		}
	}

	private List<String> buildAlerts(DashboardDto dashboard) {

		List<String> alerts = new ArrayList<>();

		if (dashboard.getOverdueServiceOrders() > 0) {
			alerts.add(dashboard.getOverdueServiceOrders() + " OS aberta(s) com prazo vencido.");
		}

		return alerts;
	}

	private void validateBusinessIsActive(User user) {
		if (user.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operacoes nao permitidas.");
		}
	}
}
