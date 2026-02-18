package com.aprimore.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Client;
import com.aprimore.models.Machine;
import com.aprimore.models.User;
import com.aprimore.models.dtos.MachineDetailsDto;
import com.aprimore.models.dtos.MachineListDto;
import com.aprimore.models.dtos.NewMachineDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.mappers.MachineMapper;
import com.aprimore.repositories.ClientRepository;
import com.aprimore.repositories.MachineRepository;

@Service
public class MachineService {

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MachineMapper machineMapper;

	public Page<MachineListDto> listByClient(UUID clientId, int page, int size, User user) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));

		validateClientAccess(client, user);

		Pageable pageable = PageRequest.of(page, size);

		return machineRepository.findByClientIdOrderByActiveDescNameAsc(clientId, pageable)
				.map(machineMapper::mapToListDto);
	}

	public void createMachine(UUID clientId, NewMachineDto dto, User user) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));

		validateClientAccess(client, user);

		Machine machine = machineMapper.mapToMachine(dto);
		machine.setClient(client);

		machineRepository.save(machine);
	}

	public void updateMachineStatus(UUID clientId, UUID machineId, User user) {

		Machine machine = machineRepository.findById(machineId)
				.orElseThrow(() -> new ResourceNotFoundException("Maquina nao encontrada"));

		validateMachineOwnership(machine, clientId, user);

		machine.setActive(!machine.isActive());

		machineRepository.save(machine);
	}

	public MachineDetailsDto findById(UUID clientId, UUID machineId, User user) {
		Machine machine = machineRepository.findById(machineId)
				.orElseThrow(() -> new ResourceNotFoundException("Maquina nao encontrada"));

		validateMachineOwnership(machine, clientId, user);

		return machineMapper.mapToDetailsDto(machine);
	}

	public void updateMachine(UUID clientId, UUID machineId, MachineDetailsDto dto, User user) {

		Machine machine = machineRepository.findById(machineId)
				.orElseThrow(() -> new ResourceNotFoundException("Maquina nao encontrada"));

		validateMachineOwnership(machine, clientId, user);

		machineMapper.updateMachineFromDetailsDto(dto, machine);
		machineRepository.save(machine);
	}

	private void validateMachineOwnership(Machine machine, UUID clientId, User user) {
		validateClientAccess(machine.getClient(), user);

		if (!machine.getClient().getId().equals(clientId)) {
			throw new AccessDeniedException("Acesso negado");
		}
	}

	private void validateClientAccess(Client client, User user) {
		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}

		if (client.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operacoes nao permitidas.");
		}
	}
}
