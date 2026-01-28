package com.aprimore.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aprimore.models.Business;
import com.aprimore.models.Client;
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
	
	public void newClient( NewClientDto newClientDto, UUID businessId) {
		
		Business business = businessRepository.findById(businessId)
		        .orElseThrow(() -> new EntityNotFoundException(
		            "Business not found"
		        ));
		
		Client newClient = clientMapper.mapToClient(newClientDto);
		newClient.setBusiness(business);
		clientRepository.save(newClient);		
		
	}
}
