package com.aprimore.models.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aprimore.models.Address;
import com.aprimore.models.Client;
import com.aprimore.models.dtos.ClientDetailsDto;
import com.aprimore.models.dtos.ClientListDto;
import com.aprimore.models.dtos.NewClientDto;

@Component
public class ClientMapper {

	private ModelMapper modelMapper = new ModelMapper();

	public Client mapToClient(NewClientDto newClientDto) {
		
		Client client = modelMapper.map(newClientDto, Client.class);
		
		Address address = new Address();
		
		address.setStreet(newClientDto.getStreet());
		address.setNumber(newClientDto.getNumber());
		address.setNeighborhood(newClientDto.getNeighborhood());
		address.setCity(newClientDto.getCity());
		address.setState(newClientDto.getState());
		address.setZipCode(newClientDto.getZipCode());
		
		client.setAddress(address);
		
		return client;
		
	}

	public ClientDetailsDto mapToClientDetailsDto(Client client) {
		
		ClientDetailsDto clientDetailsDto = modelMapper.map(client, ClientDetailsDto.class);
		clientDetailsDto.setAddress(client.getAddress().toString()); // Somente a String de Address para o DTO
		return clientDetailsDto;
	}

	public Client mapToClient(ClientDetailsDto dto, Client client) {

		client.setCnpj(dto.getCnpj().replaceAll("\\D", ""));
		client.setClientName(dto.getClientName());
		client.setClientEmail(dto.getClientEmail());
		client.setClientPhoneNumber(dto.getClientPhoneNumber().replaceAll("\\D", ""));
		client.setStandardOrderInstructions(dto.getStandardOrderInstructions());
		client.setActive(dto.isActive());

		return client;
	}
	
	public ClientListDto mapToClientListDto(Client client) {
		return modelMapper.map(client, ClientListDto.class);
	}

}
