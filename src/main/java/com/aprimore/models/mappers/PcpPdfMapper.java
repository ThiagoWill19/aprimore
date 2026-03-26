package com.aprimore.models.mappers;

import com.aprimore.models.ServiceOrder;
import com.aprimore.models.dtos.PcpPdfDto;

public class PcpPdfMapper {
    
    public static PcpPdfDto toPcpPdfDto(ServiceOrder serviceOrder) {
        return new PcpPdfDto(
            serviceOrder.getPcpSequence(), // ou outro campo que represente a sequência
            serviceOrder.getId(),
            serviceOrder.getClient().getClientName(),
            serviceOrder.getWorkName()
        );
    }
}
