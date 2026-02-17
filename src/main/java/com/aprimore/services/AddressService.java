package com.aprimore.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Address;
import com.aprimore.models.Client;
import com.aprimore.models.User;
import com.aprimore.models.dtos.UpdateAddressDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.mappers.AddressMapper;
import com.aprimore.repositories.AddressRepository;
import com.aprimore.repositories.ClientRepository;

@Service
public class AddressService {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	private AddressMapper addressMapper;

	
	public void createAddress(UUID clientId, Address address, User user) {
		
		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
		
		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}
		
		if (client.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AccessDeniedException("Empresa inativa. Operacoes nao permitidas.");
		}
		
			addressRepository.save(address);
	}
	
	
	public void updateAddress(UpdateAddressDto dto, User user) {

	    Client client = clientRepository.findById(dto.getClientId())
	            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

	    if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
	        throw new AccessDeniedException("Acesso negado");
	    }
	    
	    if (client.getBusiness().getAccountStatus() != AccountStatus.ACTIVE) {
	        throw new AccessDeniedException("Empresa inativa. Operacoes nao permitidas.");
	    }

	    Address address = client.getAddress();

	    if (address == null) {
	        throw new ResourceNotFoundException("Cliente não possui endereço cadastrado");
	    }

	    addressMapper.updateAddressFromDto(dto, address);

	    addressRepository.save(address);
	}



}
