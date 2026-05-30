package com.aprimore.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Blade;
import com.aprimore.models.Client;
import com.aprimore.models.Machine;
import com.aprimore.models.ServiceOrder;
import com.aprimore.models.User;
import com.aprimore.models.dtos.NewServiceOrderDto;
import com.aprimore.models.dtos.ServiceOrderDetailsDto;
import com.aprimore.models.dtos.ServiceOrderListDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.ServiceOrderStatus;
import com.aprimore.models.mappers.ServiceOrderMapper;
import com.aprimore.repositories.BladeRepository;
import com.aprimore.repositories.ClientRepository;
import com.aprimore.repositories.ItemRepository;
import com.aprimore.repositories.MachineRepository;
import com.aprimore.repositories.ServiceOrderRepository;

/*
 * Service responsável por gerenciar toda a regra de negócio
 * relacionada às Ordens de Serviço (ServiceOrder).
 *
 * Aqui ficam regras como:
 * - criação de OS
 * - atualização
 * - listagem
 * - validação de acesso
 * - validações de domínio
 */
@Service
public class ServiceOrderService {

	@Autowired
	private ServiceOrderRepository serviceOrderRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private BladeRepository bladeRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ServiceOrderMapper serviceOrderMapper;

	/*
	 * Retorna as máquinas de um cliente específico.
	 * Apenas máquinas pertencentes ao cliente são retornadas.
	 */
	public List<Machine> findMachinesByClient(UUID clientId, User user) {

		Client client = findClientWithAccessValidation(clientId, user);

		return machineRepository.findByClientIdOrderByActiveDescNameAsc(client.getId());
	}

	/*
	 * Retorna todos os clientes ativos da empresa do usuário logado.
	 */
	public List<Client> findClientsByBusiness(User user) {

		validateBusinessIsActive(user);
		return clientRepository.findByBusinessIdAndActiveTrueOrderByClientName(user.getBusiness().getId());
	}

	/*
	 * Retorna todas as lâminas cadastradas para a empresa.
	 */
	public List<Blade> findBladesByBusiness(User user) {

		validateBusinessIsActive(user);
		return itemRepository.findBladesByBusinessId(user.getBusiness().getId());
	}

	/*
	 * Listagem simples de OS sem filtros.
	 */
	public Page<ServiceOrderListDto> listAllByBusiness(int page, int size, User user) {
		return listAllByBusiness(page, size, null, null, null, user);
	}

	/*
	 * Listagem de OS com filtros opcionais:
	 * - busca textual
	 * - intervalo de datas
	 */
	public Page<ServiceOrderListDto> listAllByBusiness(
			int page,
			int size,
			String search,
			LocalDate startDate,
			LocalDate endDate,
			User user) {

		validateBusinessIsActive(user);

		// Proteção contra paginação inválida
		page = Math.max(page, 0);
		size = Math.min(Math.max(size, 1), 50);

		Pageable pageable = PageRequest.of(page, size);

		// Normalização do termo de busca
		String term = search == null ? null : search.trim();
		if (term != null && term.isBlank()) {
			term = null;
		}

		// Caso o termo seja um número tentamos buscar por ID da OS
		Integer serviceOrderId = parseSearchAsServiceOrderId(term);

		return serviceOrderRepository.findAllByBusinessWithFilters(
						user.getBusiness().getId(),
						term,
						serviceOrderId,
						startDate,
						endDate,
						pageable)
				.map(serviceOrderMapper::mapToListDto);
	}

	/*
	 * Caso o usuário digite um número no campo de busca,
	 * tentamos interpretar como ID da OS.
	 */
	private Integer parseSearchAsServiceOrderId(String search) {

		if (search == null || search.isBlank()) {
			return null;
		}

		try {
			return Integer.parseInt(search);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/*
	 * Lista todas as OS de um cliente específico.
	 */
	public Page<ServiceOrderListDto> listByClient(UUID clientId, int page, int size, User user) {

		Client client = findClientWithAccessValidation(clientId, user);

		page = Math.max(page, 0);
		size = Math.min(Math.max(size, 1), 50);
		Pageable pageable = PageRequest.of(page, size);

		return serviceOrderRepository.findByClientIdOrderByCreatedAtDesc(client.getId(), pageable)
				.map(serviceOrderMapper::mapToListDto);
	}

	/*
	 * Busca detalhes completos de uma OS específica.
	 */
	public ServiceOrderDetailsDto findById(UUID clientId, Long serviceOrderId, User user) {

		Client client = findClientWithAccessValidation(clientId, user);
		ServiceOrder serviceOrder = findServiceOrderWithOwnershipValidation(serviceOrderId, client.getId());
		return serviceOrderMapper.mapToDetailsDto(serviceOrder);
	}

	/*
	 * Criação de uma nova ordem de serviço.
	 */
	@Transactional
	public ServiceOrder createServiceOrder(UUID clientId, NewServiceOrderDto dto, User user) {

		Client client = findClientWithAccessValidation(clientId, user);

		// Busca máquina
		Machine machine = machineRepository.findById(dto.getMachineId())
				.orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada"));

		if(machine.isActive() == false) {
			throw new DomainRuleException("A máquina selecionada está inativa. Não é possível criar ordem de serviço para máquinas inativas.");
		}

		if (!machine.getClient().getId().equals(client.getId())) {
			throw new DomainRuleException("A máquina selecionada não pertence ao cliente informado.");
		}

		// Validação de datas
		if (dto.getDeliveryDate() != null && dto.getEntryDate() != null
				&& dto.getDeliveryDate().isBefore(dto.getEntryDate())) {
			throw new DomainRuleException("A data de entrega não pode ser anterior a data de entrada.");
		}

		List<Blade> blades = resolveAndValidateBlades(dto.getBladeIds(), user);

		String normalizedWave = normalizeAndValidateWave(dto.getTypeOfWave(), machine);

		String observations = buildServiceOrderObservations(dto.getObs(), client, machine);

		// Criação da entidade
		ServiceOrder serviceOrder = serviceOrderMapper.mapToNewEntity(
				dto,
				client,
				machine,
				blades,
				normalizedWave,
				observations);

		if (serviceOrder.getEntryDate() == null) {
			serviceOrder.setEntryDate(LocalDate.now());
		}

		serviceOrder.setStatus(ServiceOrderStatus.OPEN);

		// Define sequência no PCP
		serviceOrder.setPcpSequence(nextPcpSequence(user.getBusiness().getId()));

		int maxOrderNumber = serviceOrderRepository.findMaxOrderNumberByBusiness(client.getBusiness().getId()).orElse(0);

		serviceOrder.setOrderNumber(maxOrderNumber + 1);

		return serviceOrderRepository.save(serviceOrder);
	}

	/*
	 * Monta o campo de observações da OS combinando:
	 * - observação do usuário
	 * - instruções padrão do cliente
	 * - observações da máquina
	 */
	private String buildServiceOrderObservations(String userObs, Client client, Machine machine) {

		List<String> sections = new ArrayList<>();

		if (userObs != null && !userObs.isBlank()) {
			sections.add(userObs.trim());
		}

		if (client.getStandardOrderInstructions() != null && !client.getStandardOrderInstructions().isBlank()) {
			sections.add("Padrões do cliente:\n" + client.getStandardOrderInstructions().trim());
		}

		if (machine.getObservations() != null && !machine.getObservations().isBlank()) {
			sections.add("Observações da máquina:\n" + machine.getObservations().trim());
		}

		if (sections.isEmpty()) {
			return null;
		}

		return String.join("\n\n", sections);
	}

	@Transactional
	public void updateServiceOrder(UUID clientId, Long serviceOrderId, ServiceOrderDetailsDto dto, User user) {

		Client client = findClientWithAccessValidation(clientId, user);
		ServiceOrder serviceOrder = findServiceOrderWithOwnershipValidation(serviceOrderId, client.getId());
		Machine machine = machineRepository.findById(dto.getMachineId())
				.orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada"));

		if (!machine.getClient().getId().equals(client.getId())) {
			throw new DomainRuleException("A máquina selecionada não pertence ao cliente informado.");
		}

		if (dto.getDeliveryDate() != null && dto.getEntryDate() != null
				&& dto.getDeliveryDate().isBefore(dto.getEntryDate())) {
			throw new DomainRuleException("A data de entrega não pode ser anterior a data de entrada.");
		}

		List<Blade> blades = resolveAndValidateBlades(dto.getBladeIds(), user);
		String normalizedWave = normalizeAndValidateWave(dto.getTypeOfWave(), machine);
		ServiceOrderStatus currentStatus = serviceOrder.getStatus();
		serviceOrderMapper.updateEntityFromDetailsDto(dto, serviceOrder, machine, blades, normalizedWave);

		if (serviceOrder.getEntryDate() == null) {
			serviceOrder.setEntryDate(LocalDate.now());
		}

		/*
		 * Caso a OS volte para status OPEN,
		 * ela precisa entrar novamente no PCP.
		 */
		if (currentStatus != ServiceOrderStatus.OPEN
				&& serviceOrder.getStatus() == ServiceOrderStatus.OPEN) {
			serviceOrder.setPcpSequence(nextPcpSequence(user.getBusiness().getId()));
		}

		serviceOrderRepository.save(serviceOrder);
	}

	/*
	 * Calcula a próxima posição do PCP.
	 */
	private Integer nextPcpSequence(UUID businessId) {
		Integer currentMax = serviceOrderRepository.findMaxPcpSequenceByBusinessAndStatus(
				businessId,
				ServiceOrderStatus.OPEN);
		return currentMax + 1;
	}

	/*
	 * Busca cliente e valida acesso da empresa.
	 */
	private Client findClientWithAccessValidation(UUID clientId, User user) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}

		validateBusinessIsActive(user);

		return client;
	}

	/*
	 * Regra de segurança: empresa precisa estar ativa.
	 */
	private void validateBusinessIsActive(User user) {
		if (user.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operações não permitidas.");
		}
	}

	/*
	 * Busca OS e valida se pertence ao cliente correto.
	 */
	private ServiceOrder findServiceOrderWithOwnershipValidation(Long serviceOrderId, UUID clientId) {

		ServiceOrder serviceOrder = serviceOrderRepository.findById(serviceOrderId)
				.orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada"));

		if (!serviceOrder.getClient().getId().equals(clientId)) {
			throw new AccessDeniedException("Acesso negado");
		}

		return serviceOrder;
	}

	/*
	 * Resolve e valida as lâminas enviadas pelo usuário.
	 */
	private List<Blade> resolveAndValidateBlades(List<UUID> bladeIds, User user) {

		if (bladeIds == null || bladeIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<UUID> uniqueIds = new ArrayList<>(new LinkedHashSet<>(bladeIds));
		List<Blade> blades = bladeRepository.findAllById(uniqueIds);

		if (blades.size() != uniqueIds.size()) {
			throw new ResourceNotFoundException("Uma ou mais lâminas informadas não foram encontradas.");
		}

		for (Blade blade : blades) {
			if (!blade.getBusiness().getId().equals(user.getBusiness().getId())) {
				throw new AccessDeniedException("Acesso negado para uma ou mais lâminas selecionadas.");
			}
		}

		return blades;
	}

	/*
	 * Normaliza e valida o tipo de onda selecionado para a máquina.
	 */
	private String normalizeAndValidateWave(String selectedWave, Machine machine) {

		if (selectedWave == null || selectedWave.isBlank()) {
			throw new DomainRuleException("Tipo de onda é obrigatório.");
		}

		String normalizedSelectedWave = selectedWave.trim().toUpperCase(Locale.ROOT);
		List<String> machineWaves = parseMachineWaves(machine.getWave());

		if (machineWaves.isEmpty()) {
			throw new DomainRuleException("A máquina selecionada não possui tipos de onda configurados.");
		}

		if (!machineWaves.contains(normalizedSelectedWave)) {
			throw new DomainRuleException("Tipo de onda invalido para a maquina selecionada. Opcoes: " + String.join(", ", machineWaves));
		}

		return normalizedSelectedWave;
	}

	/*
	 * Faz o parsing do campo de ondas da máquina, que pode conter múltiplas opções
	 * separadas por vírgula, barra, ponto e vírgula, espaço ou hífen.
	 */
	private List<String> parseMachineWaves(String rawWave) {
		if (rawWave == null || rawWave.isBlank()) {
			return new ArrayList<>();
		}
		return java.util.Arrays.stream(rawWave.toUpperCase(Locale.ROOT).split("[,;/|\\s-]+"))
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.distinct()
				.collect(java.util.stream.Collectors.toList());
	}
}
