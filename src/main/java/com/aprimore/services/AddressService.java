package com.aprimore.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Address;
import com.aprimore.models.Client;
import com.aprimore.models.User;
import com.aprimore.repositories.AddressRepository;
import com.aprimore.repositories.ClientRepository;

@Service
public class AddressService {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	public void createAddress(UUID clientId, Address address, User user) {
		
		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
		
		if (!client.getBusiness().getId().equals(user.getBusiness().getId())) {
			throw new AccessDeniedException("Acesso negado");
		}
		
			addressRepository.save(address);
	}

}
