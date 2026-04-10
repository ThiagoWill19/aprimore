package com.aprimore.models.mappers;

import com.aprimore.models.dtos.ServiceOrderPdfDto;
import com.aprimore.models.Blade;
import com.aprimore.models.Machine;
import com.aprimore.models.ServiceOrder;

import java.util.stream.Collectors;

public class ServiceOrderPdfMapper {

    public static ServiceOrderPdfDto toDto(ServiceOrder os) {
        ServiceOrderPdfDto dto = new ServiceOrderPdfDto();

        dto.setId(os.getOrderNumber());
        dto.setWorkName(os.getWorkName());
        dto.setReference(os.getReference());
        dto.setEntryDate(os.getEntryDate());
        dto.setDeliveryDate(os.getDeliveryDate());

        dto.setType(os.getType());
        dto.setArrangement(os.getArrangement());
        dto.setTypeOfWave(os.getTypeOfWave());

        dto.setServicesToBePerformed(os.getServicesToBePerformed());
        dto.setObs(os.getObs());

        // Client
        if (os.getClient() != null) {
            dto.setClientName(os.getClient().getClientName());
        }

        // Machine
        dto.setMachine(mapMachine(os.getMachine()));

        // Blades
        dto.setBlades(os.getBlades());

        return dto;
    }

    private static ServiceOrderPdfDto.MachinePdfDto mapMachine(Machine machine) {
        if (machine == null) return null;

        ServiceOrderPdfDto.MachinePdfDto dto = new ServiceOrderPdfDto.MachinePdfDto();

        dto.setName(machine.getName());

        boolean isRotary = machine instanceof com.aprimore.models.RotaryMachine;
        dto.setRotary(isRotary);

        if (isRotary) {
            var rotary = (com.aprimore.models.RotaryMachine) machine;

            dto.setCenterLine(rotary.getCenterLine());
            dto.setDiameter(rotary.getDiameter());
            dto.setDistanceBetweenHolesInStraightLine(rotary.getDistanceBetweenHolesInStraightLine());
            dto.setDistanceBetweenHolesInCurvedDirection(rotary.getDistanceBetweenHolesInCurvedDirection());
            dto.setReduction(rotary.getReduction());
            dto.setTotalLengthCylinder(rotary.getTotalLengthCylinder());

        } else if (machine instanceof com.aprimore.models.FlatMachine) {
            var flat = (com.aprimore.models.FlatMachine) machine;

            dto.setMaxSheetDimension(flat.getMaxSheetDimension());
        }

        return dto;
    }
}