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

/*
 * Service responsável por montar e gerenciar as informações exibidas no Dashboard.
 * Aqui centralizamos as regras de negócio relacionadas à visualização de dados
 * resumidos do sistema (contadores, alertas e prioridades de produção).
 */
@Service
public class DashboardService {

	@Autowired
	private ServiceOrderRepository serviceOrderRepository;

    /*
	 * Método principal responsável por carregar todos os dados necessários
	 * para montar o dashboard da aplicação.
	 */
	public DashboardDto loadDashboard(User user) {

		validateBusinessIsActive(user);

		UUID businessId = user.getBusiness().getId();

		// Data atual usada para verificar atrasos
		LocalDate today = LocalDate.now();

		// DTO que será retornado ao controller com todos os dados do dashboard
		DashboardDto dashboard = new DashboardDto();
		
		dashboard.setOpenServiceOrders(
				serviceOrderRepository.countByClientBusinessIdAndStatus(businessId, ServiceOrderStatus.OPEN));

		dashboard.setClosedServiceOrders(
				serviceOrderRepository.countByClientBusinessIdAndStatus(businessId, ServiceOrderStatus.CLOSED));

		// Conta quantas ordens abertas já passaram da data de entrega
		dashboard.setOverdueServiceOrders(
				serviceOrderRepository.countByClientBusinessIdAndStatusAndDeliveryDateBefore(
						businessId,
						ServiceOrderStatus.OPEN,
						today));

		// Carrega as OS consideradas prioritárias (ex: sequência de produção)
		dashboard.setPriorityServiceOrders(loadPriorityServiceOrders(businessId, today));

		// Gera alertas baseados nos dados carregados
		dashboard.setAlerts(buildAlerts(dashboard));

		return dashboard;
	}


	/*
	 * Carrega as ordens de serviço prioritárias para exibição no dashboard.
	 * Essas ordens geralmente representam a sequência de produção (PCP).
	 */
	private List<DashboardPriorityServiceOrderDto> loadPriorityServiceOrders(UUID businessId, LocalDate today) {

		// Busca no banco as OS abertas e ordenadas por prioridade
		List<ServiceOrder> priorityOrders = serviceOrderRepository
				.findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN);

		// Converte as entidades para DTOs usando a API de Streams
		return priorityOrders.stream().map(serviceOrder -> {
			DashboardPriorityServiceOrderDto dto = new DashboardPriorityServiceOrderDto();
			dto.setId(serviceOrder.getId());
			dto.setServiceOrderNumber(serviceOrder.getOrderNumber());
			dto.setClientId(serviceOrder.getClient().getId());
			dto.setClientName(serviceOrder.getClient().getClientName());
			dto.setWorkName(serviceOrder.getWorkName());
			dto.setEntryDate(serviceOrder.getEntryDate());
			dto.setDeliveryDate(serviceOrder.getDeliveryDate());
			dto.setOverdue(serviceOrder.getDeliveryDate() != null && serviceOrder.getDeliveryDate().isBefore(today));
			dto.setPcpSequence(serviceOrder.getPcpSequence());
			return dto;
		}).collect(Collectors.toList());
	}

	/*
	 * Atualiza a sequência de prioridade das ordens de serviço no PCP.
	 *
	 * Esse método recebe uma lista de IDs já ordenados (ex: após drag-and-drop no frontend)
	 * e atualiza a sequência no banco respeitando essa ordem.
	 */
	@Transactional
	public void updatePcpSequence(List<Long> orderedIds, User user) {

		validateBusinessIsActive(user);

		// Se a lista vier vazia ou nula não há nada para atualizar
		if (orderedIds == null || orderedIds.isEmpty()) {
			return;
		}

		UUID businessId = user.getBusiness().getId();

		/*
		 * Remove possíveis IDs duplicados mantendo a ordem original.
		 * LinkedHashSet preserva a ordem de inserção.
		 */
		List<Long> uniqueIds = new ArrayList<>(new LinkedHashSet<>(orderedIds));

		/*
		 * Busca no banco apenas as OS que:
		 * - pertencem à empresa do usuário
		 * - estão abertas
		 * - possuem os IDs enviados
		 */
		List<ServiceOrder> requestedOrders = serviceOrderRepository.findByBusinessAndStatusAndIdIn(
				businessId,
				ServiceOrderStatus.OPEN,
				uniqueIds);

		/*
		 * Se a quantidade retornada for diferente da enviada,
		 * significa que algum ID não pertence à empresa ou não está aberto.
		 * Nesse caso bloqueamos a operação por segurança.
		 */
		if (requestedOrders.size() != uniqueIds.size()) {
			throw new AccessDeniedException("Sequência inválida para a empresa logada.");
		}

		// Cria um Map para acessar rapidamente a OS pelo ID
		Map<Long, ServiceOrder> orderById = requestedOrders.stream()
				.collect(Collectors.toMap(ServiceOrder::getId, serviceOrder -> serviceOrder));

		/*
		 * Atualiza a sequência do PCP respeitando a ordem enviada pelo frontend
		 */
		int sequence = 1;
		for (Long serviceOrderId : uniqueIds) {
			ServiceOrder serviceOrder = orderById.get(serviceOrderId);
			serviceOrder.setPcpSequence(sequence++);
		}

		/*
		 * Agora tratamos as outras OS abertas que não estavam na lista enviada.
		 * Elas recebem sequência após as priorizadas.
		 */
		List<ServiceOrder> allOpenOrders = serviceOrderRepository.findPriorityByBusinessAndStatus(
				businessId,
				ServiceOrderStatus.OPEN);
		for (ServiceOrder serviceOrder : allOpenOrders) {
			if (!orderById.containsKey(serviceOrder.getId())) {
				serviceOrder.setPcpSequence(sequence++);
			}
		}
	}


	/*
	 * Gera mensagens de alerta baseadas nas métricas do dashboard.
	 */
	private List<String> buildAlerts(DashboardDto dashboard) {

		List<String> alerts = new ArrayList<>();

		// Se existir qualquer OS atrasada, adiciona um alerta
		if (dashboard.getOverdueServiceOrders() > 0) {
			alerts.add(dashboard.getOverdueServiceOrders() + " OS aberta(s) com prazo vencido.");
		}

		return alerts;
	}

	/*
	 * Regra de segurança do sistema.
	 * Nenhuma operação é permitida se a empresa estiver inativa.
	 */
	private void validateBusinessIsActive(User user) {
		if (user.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operações nao permitidas.");
		}
	}
}
