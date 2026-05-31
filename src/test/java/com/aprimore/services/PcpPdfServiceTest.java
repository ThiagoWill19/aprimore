package com.aprimore.services;

import com.aprimore.models.ServiceOrder;
import com.aprimore.models.Client;
import com.aprimore.models.dtos.PcpPdfDto;
import com.aprimore.models.enuns.ServiceOrderStatus;
import com.aprimore.repositories.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PcpPdfServiceTest {

    @Mock
    private TemplateService templateService;
    @Mock
    private PdfService pdfService;
    @Mock
    private ServiceOrderRepository repository;

    @InjectMocks
    private PcpPdfService pcpPdfService;

    @Test
    @DisplayName("Deve gerar o PDF do PCP com sucesso")
    void gerarPdf_Success() {
        // Arrange
        UUID businessId = UUID.randomUUID();

        Client client = new Client();
        client.setClientName("Cliente de Teste");

        ServiceOrder order = new ServiceOrder();
        order.setOrderNumber(123);
        order.setPcpSequence(1);
        order.setClient(client);
        order.setWorkName("Trabalho de Teste");
        List<ServiceOrder> orders = List.of(order);

        String html = "<html><body>PCP</body></html>";
        byte[] pdfBytes = {1, 2, 3};

        when(repository.findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN)).thenReturn(orders);
        when(templateService.renderPcpPrioridadeDoDia(any())).thenReturn(html);
        when(pdfService.gerarPdf(html)).thenReturn(pdfBytes);

        // Act
        byte[] result = pcpPdfService.gerarPdf(businessId);

        // Assert
        ArgumentCaptor<List<PcpPdfDto>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN);
        verify(templateService).renderPcpPrioridadeDoDia(captor.capture());
        assertEquals(1, captor.getValue().size());
        verify(pdfService).gerarPdf(html);
        assertArrayEquals(pdfBytes, result);
    }
}