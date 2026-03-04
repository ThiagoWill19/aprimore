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

	public List<Machine> findMachinesByClient(UUID clientId, User user) {

		Client client = findClientWithAccessValidation(clientId, user);

		return machineRepository.findByClientIdOrderByActiveDescNameAsc(client.getId());
	}

	public List<Client> findClientsByBusiness(User user) {

		validateBusinessIsActive(user);
		return clientRepository.findByBusinessIdAndActiveTrueOrderByClientName(user.getBusiness().getId());
	}

	public List<Blade> findBladesByBusiness(User user) {

		validateBusinessIsActive(user);
		return itemRepository.findBladesByBusinessId(user.getBusiness().getId());
	}

	public Page<ServiceOrderListDto> listByClient(UUID clientId, int page, int size, User user) {

		Client client = findClientWithAccessValidation(clientId, user);

		page = Math.max(page, 0);
		size = Math.min(Math.max(size, 1), 50);
		Pageable pageable = PageRequest.of(page, size);

		return serviceOrderRepository.findByClientIdOrderByCreatedAtDesc(client.getId(), pageable)
				.map(serviceOrderMapper::mapToListDto);
	}

	public ServiceOrderDetailsDto findById(UUID clientId, Long serviceOrderId, User user) {

		Client client = findClientWithAccessValidation(clientId, user);
		ServiceOrder serviceOrder = findServiceOrderWithOwnershipValidation(serviceOrderId, client.getId());
		return serviceOrderMapper.mapToDetailsDto(serviceOrder);
	}

	@Transactional
	public ServiceOrder createServiceOrder(UUID clientId, NewServiceOrderDto dto, User user) {

		Client client = findClientWithAccessValidation(clientId, user);
		Machine machine = machineRepository.findById(dto.getMachineId())
				.orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada"));

		if(machine.isActive() == false) {
			throw new DomainRuleException("A máquina selecionada está inativa. Não é possível criar ordem de serviço para máquinas inativas.");
		}

		if (!machine.getClient().getId().equals(client.getId())) {
			throw new DomainRuleException("A máquina selecionada não pertence ao cliente informado.");
		}

		if (dto.getDeliveryDate() != null && dto.getEntryDate() != null
				&& dto.getDeliveryDate().isBefore(dto.getEntryDate())) {
			throw new DomainRuleException("A data de entrega não pode ser anterior a data de entrada.");
		}

		List<Blade> blades = resolveAndValidateBlades(dto.getBladeIds(), user);
		String normalizedWave = normalizeAndValidateWave(dto.getTypeOfWave(), machine);
		String observations = buildServiceOrderObservations(dto.getObs(), client, machine);

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

		return serviceOrderRepository.save(serviceOrder);
	}

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
		serviceOrderMapper.updateEntityFromDetailsDto(dto, serviceOrder, machine, blades, normalizedWave);

		if (serviceOrder.getEntryDate() == null) {
			serviceOrder.setEntryDate(LocalDate.now());
		}

		serviceOrderRepository.save(serviceOrder);
	}

	private Client findClientWithAccessValidation(UUID clientId, User user) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}

		validateBusinessIsActive(user);

		return client;
	}

	private void validateBusinessIsActive(User user) {
		if (user.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operações não permitidas.");
		}
	}

	private ServiceOrder findServiceOrderWithOwnershipValidation(Long serviceOrderId, UUID clientId) {

		ServiceOrder serviceOrder = serviceOrderRepository.findById(serviceOrderId)
				.orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada"));

		if (!serviceOrder.getClient().getId().equals(clientId)) {
			throw new AccessDeniedException("Acesso negado");
		}

		return serviceOrder;
	}

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

	private List<String> parseMachineWaves(String rawWave) {

		if (rawWave == null || rawWave.isBlank()) {
			return new ArrayList<>();
		}

		LinkedHashSet<String> waves = new LinkedHashSet<>();
		String[] tokens = rawWave.toUpperCase(Locale.ROOT).split("[,;/|\\s-]+");
		for (String token : tokens) {
			String normalizedToken = token.trim();
			if (!normalizedToken.isBlank()) {
				waves.add(normalizedToken);
			}
		}

		if (waves.isEmpty()) {
			String fallback = rawWave.trim().toUpperCase(Locale.ROOT);
			if (!fallback.isBlank()) {
				waves.add(fallback);
			}
		}

		return new ArrayList<>(waves);
	}
}
