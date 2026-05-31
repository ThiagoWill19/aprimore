package com.aprimore.services;

import com.aprimore.models.dtos.PcpPdfDto;
import com.aprimore.models.dtos.ServiceOrderPdfDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private TemplateService templateService;

    @Test
    @DisplayName("Deve renderizar o template da Ordem de Serviço corretamente")
    void renderServiceOrder_Success() {
        // Arrange
        ServiceOrderPdfDto dto = new ServiceOrderPdfDto();
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        // Act
        templateService.renderServiceOrder(dto);

        // Assert
        verify(templateEngine).process(eq("user/service-order-pdf"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertEquals(dto, capturedContext.getVariable("os"));
    }

    @Test
    @DisplayName("Deve renderizar o template do PCP corretamente")
    void renderPcpPrioridadeDoDia_Success() {
        // Arrange
        List<PcpPdfDto> pcpList = List.of(new PcpPdfDto(1, 101, "Empresa Teste", "Trabalho Teste"));
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        // Act
        templateService.renderPcpPrioridadeDoDia(pcpList);

        // Assert
        verify(templateEngine).process(eq("user/pcp-pdf"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertEquals(pcpList, capturedContext.getVariable("pcp"));
    }
}