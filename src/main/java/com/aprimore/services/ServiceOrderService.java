package com.aprimore.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.aprimore.models.enuns.AccountStatus;
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

	public List<Machine> findMachinesByClient(UUID clientId, User user) {

		Client client = findClientWithAccessValidation(clientId, user);

		return machineRepository.findByClientIdOrderByActiveDescNameAsc(client.getId());
	}

	public List<Blade> findBladesByBusiness(User user) {

		validateBusinessIsActive(user);
		return itemRepository.findBladesByBusinessId(user.getBusiness().getId());
	}

	@Transactional
	public ServiceOrder createServiceOrder(UUID clientId, NewServiceOrderDto dto, User user) {

		Client client = findClientWithAccessValidation(clientId, user);
		Machine machine = machineRepository.findById(dto.getMachineId())
				.orElseThrow(() -> new ResourceNotFoundException("Maquina nao encontrada"));

		if (!machine.getClient().getId().equals(client.getId())) {
			throw new DomainRuleException("A maquina selecionada nao pertence ao cliente informado.");
		}

		if (dto.getDeliveryDate() != null && dto.getEntryDate() != null
				&& dto.getDeliveryDate().isBefore(dto.getEntryDate())) {
			throw new DomainRuleException("A data de entrega nao pode ser anterior a data de entrada.");
		}

		List<Blade> blades = resolveAndValidateBlades(dto.getBladeIds(), user);

		ServiceOrder serviceOrder = new ServiceOrder();
		serviceOrder.setWorkName(dto.getWorkName());
		serviceOrder.setReference(dto.getReference());
		serviceOrder.setEntryDate(dto.getEntryDate());
		serviceOrder.setDeliveryDate(dto.getDeliveryDate());
		serviceOrder.setCreatedAt(LocalDateTime.now());
		serviceOrder.setType(machine.getClass().getSimpleName().replace("Machine", "").toUpperCase());
		serviceOrder.setInternalMeasure(dto.getInternalMeasure());
		serviceOrder.setMachine(machine);
		serviceOrder.setClient(client);
		serviceOrder.setArrangement(dto.getArrangement());
		serviceOrder.setTypeOfWave(machine.getWave());
		serviceOrder.setServicesToBePerformed(dto.getServicesToBePerformed());
		serviceOrder.setObs(dto.getObs());
		serviceOrder.setBlades(blades);

		if (serviceOrder.getEntryDate() == null) {
			serviceOrder.setEntryDate(LocalDate.now());
		}

		return serviceOrderRepository.save(serviceOrder);
	}

	private Client findClientWithAccessValidation(UUID clientId, User user) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}

		validateBusinessIsActive(user);

		return client;
	}

	private void validateBusinessIsActive(User user) {
		if (user.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operacoes nao permitidas.");
		}
	}

	private List<Blade> resolveAndValidateBlades(List<UUID> bladeIds, User user) {

		if (bladeIds == null || bladeIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<UUID> uniqueIds = new ArrayList<>(new LinkedHashSet<>(bladeIds));
		List<Blade> blades = bladeRepository.findAllById(uniqueIds);

		if (blades.size() != uniqueIds.size()) {
			throw new ResourceNotFoundException("Uma ou mais laminas informadas nao foram encontradas.");
		}

		for (Blade blade : blades) {
			if (!blade.getBusiness().getId().equals(user.getBusiness().getId())) {
				throw new AccessDeniedException("Acesso negado para uma ou mais laminas selecionadas.");
			}
		}

		return blades;
	}
}
