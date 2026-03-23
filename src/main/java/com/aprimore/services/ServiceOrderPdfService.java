package com.aprimore.services;

import org.springframework.stereotype.Service;

import com.aprimore.models.ServiceOrder;
import com.aprimore.models.dtos.ServiceOrderPdfDto;
import com.aprimore.models.mappers.ServiceOrderPdfMapper;
import com.aprimore.repositories.ServiceOrderRepository;

@Service
public class ServiceOrderPdfService {

    private final TemplateService templateService;
    private final PdfService pdfService;
    private final ServiceOrderRepository repository;

     public ServiceOrderPdfService(
        TemplateService templateService,
        PdfService pdfService,
        ServiceOrderRepository repository
    ) {
        this.templateService = templateService;
        this.pdfService = pdfService;
        this.repository = repository;
    }

    public byte[] gerarPdf(Long id) {
        ServiceOrder os = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        ServiceOrderPdfDto dto = ServiceOrderPdfMapper.toDto(os);

        String html = templateService.renderServiceOrder(dto);

        return pdfService.gerarPdf(html);
    }
    
}
