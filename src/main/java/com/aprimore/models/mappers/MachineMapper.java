package com.aprimore.models.mappers;

import org.springframework.stereotype.Component;

import com.aprimore.models.FlatMachine;
import com.aprimore.models.Machine;
import com.aprimore.models.RotaryMachine;
import com.aprimore.models.dtos.MachineDetailsDto;
import com.aprimore.models.dtos.MachineListDto;
import com.aprimore.models.dtos.NewMachineDto;

@Component
public class MachineMapper {

	public MachineListDto mapToListDto(Machine machine) {

		MachineListDto dto = new MachineListDto();
		dto.setId(machine.getId());
		dto.setName(machine.getName());
		dto.setWave(machine.getWave());
		dto.setActive(machine.isActive());
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
			machine.setActive(true);
			machine.setDistanceBetweenHolesInStraightLine(dto.getDistanceBetweenHolesInStraightLine());
			machine.setDistanceBetweenHolesInCurvedDirection(dto.getDistanceBetweenHolesInCurvedDirection());
			machine.setHasMeshPattern(dto.isHasMeshPattern());
			return machine;
		}

		FlatMachine machine = new FlatMachine();
		machine.setName(dto.getName());
		machine.setMaxSheetDimension(dto.getMaxSheetDimension());
		machine.setWave(dto.getWave());;
		machine.setDescription(dto.getDescription());
		machine.setObservations(dto.getObservations());
		machine.setActive(true);
		return machine;
	}

	public MachineDetailsDto mapToDetailsDto(Machine machine) {

		MachineDetailsDto dto = new MachineDetailsDto();
		dto.setId(machine.getId());
		dto.setClientId(machine.getClient().getId());
		dto.setClientName(machine.getClient().getClientName());
		dto.setName(machine.getName());
		dto.setWave(machine.getWave());
		dto.setDescription(machine.getDescription());
		dto.setObservations(machine.getObservations());
		dto.setActive(machine.isActive());

		if (machine instanceof RotaryMachine rotaryMachine) {
			dto.setType("ROTARY");
			dto.setDiameter(rotaryMachine.getDiameter());
			dto.setCenterLine(rotaryMachine.getCenterLine());
			dto.setTotalLengthCylinder(rotaryMachine.getTotalLengthCylinder());
			dto.setReduction(rotaryMachine.getReduction());
			dto.setDistanceBetweenHolesInStraightLine(rotaryMachine.getDistanceBetweenHolesInStraightLine());
			dto.setDistanceBetweenHolesInCurvedDirection(rotaryMachine.getDistanceBetweenHolesInCurvedDirection());
			dto.setHasMeshPattern(rotaryMachine.isHasMeshPattern());

			return dto;
		}

		FlatMachine flatMachine = (FlatMachine) machine;
		dto.setType("FLAT");
		dto.setMaxSheetDimension(flatMachine.getMaxSheetDimension());
		return dto;
	}

	public void updateMachineFromDetailsDto(MachineDetailsDto dto, Machine machine) {
		machine.setName(dto.getName());
		machine.setWave(dto.getWave());
		machine.setDescription(dto.getDescription());
		machine.setObservations(dto.getObservations());
		machine.setActive(dto.isActive());

		if (machine instanceof RotaryMachine rotaryMachine) {
			rotaryMachine.setDiameter(dto.getDiameter());
			rotaryMachine.setCenterLine(dto.getCenterLine());
			rotaryMachine.setTotalLengthCylinder(dto.getTotalLengthCylinder());
			rotaryMachine.setReduction(dto.getReduction());
			rotaryMachine.setDistanceBetweenHolesInStraightLine(dto.getDistanceBetweenHolesInStraightLine());
			rotaryMachine.setDistanceBetweenHolesInCurvedDirection(dto.getDistanceBetweenHolesInCurvedDirection());
			rotaryMachine.setHasMeshPattern(dto.isHasMeshPattern());
			return;
		}

		FlatMachine flatMachine = (FlatMachine) machine;
		flatMachine.setMaxSheetDimension(dto.getMaxSheetDimension());
	}
}
