package com.aprimore.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aprimore.models.ServiceOrder;
import com.aprimore.models.dtos.PcpPdfDto;
import com.aprimore.models.enuns.ServiceOrderStatus;
import com.aprimore.models.mappers.PcpPdfMapper;
import com.aprimore.repositories.ServiceOrderRepository;

@Service
public class PcpPdfService {
    
    private final TemplateService templateService;
    private final PdfService pdfService;
    private final ServiceOrderRepository repository;

     public PcpPdfService(
        TemplateService templateService,
        PdfService pdfService,
        ServiceOrderRepository repository
    ) {
        this.templateService = templateService;
        this.pdfService = pdfService;
        this.repository = repository;
    }

    public byte[] gerarPdf(UUID businessId) {

        // Busca no banco as OS abertas e ordenadas por prioridade
		List<ServiceOrder> priorityOrders = repository
				.findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN);

        List<PcpPdfDto> pcpDtos = priorityOrders.stream()
                .map(PcpPdfMapper::toPcpPdfDto)
                .toList();  

        String html = templateService.renderPcpPrioridadeDoDia(pcpDtos);

        return pdfService.gerarPdf(html);
    }
}
