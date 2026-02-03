package com.aprimore.models.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aprimore.models.Client;
import com.aprimore.models.dtos.ClientDetailsDto;
import com.aprimore.models.dtos.ClientListDto;
import com.aprimore.models.dtos.NewClientDto;

@Component
public class ClientMapper {

	private ModelMapper modelMapper = new ModelMapper();

	public Client mapToClient(NewClientDto newClientDto) {
		return modelMapper.map(newClientDto, Client.class);
	}

	public ClientDetailsDto mapToClientDetailsDto(Client client) {
		return modelMapper.map(client, ClientDetailsDto.class);
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
