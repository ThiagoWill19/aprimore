package com.aprimore.models.mappers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.aprimore.models.Blade;
import com.aprimore.models.Client;
import com.aprimore.models.Machine;
import com.aprimore.models.ServiceOrder;
import com.aprimore.models.dtos.NewServiceOrderDto;
import com.aprimore.models.dtos.ServiceOrderDetailsDto;
import com.aprimore.models.dtos.ServiceOrderListDto;

@Component
public class ServiceOrderMapper {

	public ServiceOrderListDto mapToListDto(ServiceOrder serviceOrder) {
		ServiceOrderListDto dto = new ServiceOrderListDto();
		dto.setId(serviceOrder.getId());
		dto.setWorkName(serviceOrder.getWorkName());
		dto.setReference(serviceOrder.getReference());
		dto.setMachineName(serviceOrder.getMachine() != null ? serviceOrder.getMachine().getName() : null);
		dto.setDeliveryDate(serviceOrder.getDeliveryDate());
		dto.setCreatedAt(serviceOrder.getCreatedAt());
		return dto;
	}

	public ServiceOrderDetailsDto mapToDetailsDto(ServiceOrder serviceOrder) {
		ServiceOrderDetailsDto dto = new ServiceOrderDetailsDto();
		dto.setId(serviceOrder.getId());
		dto.setClientId(serviceOrder.getClient().getId());
		dto.setWorkName(serviceOrder.getWorkName());
		dto.setReference(serviceOrder.getReference());
		dto.setEntryDate(serviceOrder.getEntryDate());
		dto.setDeliveryDate(serviceOrder.getDeliveryDate());
		dto.setCreatedAt(serviceOrder.getCreatedAt());
		dto.setInternalMeasure(serviceOrder.getInternalMeasure());
		dto.setMachineId(serviceOrder.getMachine() != null ? serviceOrder.getMachine().getId() : null);
		dto.setTypeOfWave(serviceOrder.getTypeOfWave());
		dto.setArrangement(serviceOrder.getArrangement());
		dto.setServicesToBePerformed(serviceOrder.getServicesToBePerformed());
		dto.setObs(serviceOrder.getObs());
		dto.setBladeIds(serviceOrder.getBlades().stream().map(Blade::getId).toList());
		return dto;
	}

	public ServiceOrder mapToNewEntity(
			NewServiceOrderDto dto,
			Client client,
			Machine machine,
			List<Blade> blades,
			String normalizedWave,
			String observations) {

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
		serviceOrder.setTypeOfWave(normalizedWave);
		serviceOrder.setServicesToBePerformed(dto.getServicesToBePerformed());
		serviceOrder.setObs(observations);
		serviceOrder.setBlades(blades);
		return serviceOrder;
	}

	public void updateEntityFromDetailsDto(
			ServiceOrderDetailsDto dto,
			ServiceOrder serviceOrder,
			Machine machine,
			List<Blade> blades,
			String normalizedWave) {

		serviceOrder.setWorkName(dto.getWorkName());
		serviceOrder.setReference(dto.getReference());
		serviceOrder.setEntryDate(dto.getEntryDate());
		serviceOrder.setDeliveryDate(dto.getDeliveryDate());
		serviceOrder.setInternalMeasure(dto.getInternalMeasure());
		serviceOrder.setMachine(machine);
		serviceOrder.setArrangement(dto.getArrangement());
		serviceOrder.setServicesToBePerformed(dto.getServicesToBePerformed());
		serviceOrder.setObs(dto.getObs());
		serviceOrder.setBlades(blades);
		serviceOrder.setType(machine.getClass().getSimpleName().replace("Machine", "").toUpperCase());
		serviceOrder.setTypeOfWave(normalizedWave);
	}
}
