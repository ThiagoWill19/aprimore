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
import com.aprimore.models.dtos.MachineListDto;
import com.aprimore.models.dtos.NewMachineDto;
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

	public Page<MachineListDto> listByClient(
			UUID clientId,
			int page,
			int size,
			User user
	) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}

		Pageable pageable = PageRequest.of(page, size);

		return machineRepository
				.findByClientIdOrderByName(clientId, pageable)
				.map(machineMapper::mapToListDto);
	}

	public void createMachine(UUID clientId, NewMachineDto dto, User user) {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}

		Machine machine = machineMapper.mapToMachine(dto);
		machine.setClient(client);

		machineRepository.save(machine);
	}
}
