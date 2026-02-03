package com.aprimore.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Business;
import com.aprimore.models.Client;
import com.aprimore.models.User;
import com.aprimore.models.dtos.ClientDetailsDto;
import com.aprimore.models.dtos.ClientListDto;
import com.aprimore.models.dtos.NewClientDto;
import com.aprimore.models.mappers.ClientMapper;
import com.aprimore.repositories.BusinessRepository;
import com.aprimore.repositories.ClientRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private ClientMapper clientMapper;

	public Client newClient(NewClientDto newClientDto, UUID businessId){

		Business business = businessRepository.findById(businessId)
				.orElseThrow(() -> new EntityNotFoundException("Business not found"));

		newClientDto.setCnpj(newClientDto.getCnpj().replaceAll("\\D", ""));
		newClientDto.setClientPhoneNumber(newClientDto.getClientPhoneNumber().replaceAll("\\D", ""));

		Client newClient = clientMapper.mapToClient(newClientDto);
		newClient.setBusiness(business);

		try {
			return clientRepository.save(newClient);
		} catch (Exception e) {
			throw new DomainRuleException(
					"Ops, você informou dados de uma empresa já cadastrada! Verifique os dados informados.");
		}

	}

	public ClientDetailsDto findById(UUID clientId, User user) throws Exception {

		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID informado!"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {

			throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
		}

		ClientDetailsDto clientDetailsDto = clientMapper.mapToClientDetailsDto(client);

		clientDetailsDto.setQntMachines(client.getMachines().size());
		clientDetailsDto.setQntServiceOrder(client.getServiceOrders().size());

		return clientDetailsDto;

	}

	public void updateClient(ClientDetailsDto clientDetailsDto, User user) throws Exception {

		Client client = clientRepository.findById(clientDetailsDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Client com id informado não encontrado"));

		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {

			throw new AccessDeniedException("Você não tem permissão para alterar este recurso.");
		}

		client = clientMapper.mapToClient(clientDetailsDto, client);

		try {

			clientRepository.saveAndFlush(client);

		} catch (DataIntegrityViolationException e) {
			throw new DomainRuleException(
					"Ops, você informou dados de uma empresa já cadastrada! Verifique os dados informados.");
		}

	}
	
	public Page<ClientListDto> findAllClientsByBusiness(
			int pageNum,
			int size,
			User user
	) {

		Pageable pageable = PageRequest.of(pageNum, size);

		Page<Client> page = clientRepository
				.findByBusinessIdOrderByClientName(
						user.getBusiness().getId(),
						pageable
				);

		return page.map(clientMapper::mapToClientListDto);
	}

}
