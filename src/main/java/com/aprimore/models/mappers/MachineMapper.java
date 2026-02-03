package com.aprimore.models.mappers;

import org.springframework.stereotype.Component;

import com.aprimore.models.FlatMachine;
import com.aprimore.models.Machine;
import com.aprimore.models.RotaryMachine;
import com.aprimore.models.dtos.MachineListDto;
import com.aprimore.models.dtos.NewMachineDto;

@Component
public class MachineMapper {

	public MachineListDto mapToListDto(Machine machine) {

		MachineListDto dto = new MachineListDto();
		dto.setId(machine.getId());
		dto.setName(machine.getName());
		dto.setWave(machine.getWave());
		dto.setType(machine instanceof RotaryMachine ? "ROTARY" : "FLAT");

		return dto;
	}

	public Machine mapToMachine(NewMachineDto dto) {

		if ("ROTARY".equals(dto.getType())) {
			RotaryMachine machine = new RotaryMachine();
			machine.setName(dto.getName());
			machine.setWave(dto.getWave());;
			machine.setDescription(dto.getDescription());
			machine.setObservations(dto.getObservations());;
			machine.setDiameter(dto.getDiameter());
			machine.setCenterLine(dto.getCenterLine());
			machine.setTotalLengthCylinder(dto.getTotalLengthCylinder());
			machine.setReduction(dto.getReduction());
			machine.setDrillingInformation(dto.getDrillingInformation());
			return machine;
		}

		FlatMachine machine = new FlatMachine();
		machine.setName(dto.getName());
		machine.setMaxSheetDimension(dto.getMaxSheetDimension());
		return machine;
	}
}
