package com.aprimore.services;

import com.aprimore.models.Business;
import com.aprimore.models.Client;
import com.aprimore.models.ServiceOrder;
import com.aprimore.models.dtos.ServiceOrderPdfDto;
import com.aprimore.repositories.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOrderPdfServiceTest {

    @Mock
    private TemplateService templateService;
    @Mock
    private PdfService pdfService;
    @Mock
    private ServiceOrderRepository repository;

    @InjectMocks
    private ServiceOrderPdfService serviceOrderPdfService;

    @Test
    @DisplayName("Deve gerar o PDF da Ordem de Serviço com sucesso")
    void gerarPdf_Success() {
        // Arrange
        Long osId = 1L;

        Business business = new Business();
        business.setName("Empresa Teste");

        Client client = new Client();
        client.setClientName("Cliente Teste");
        client.setBusiness(business);

        ServiceOrder os = new ServiceOrder();
        os.setId(osId);
        os.setOrderNumber(101);
        os.setClient(client);
        os.setEntryDate(LocalDate.now());
        os.setWorkName("Trabalho Teste");

        String html = "<html><body>OS</body></html>";
        byte[] pdfBytes = {1, 2, 3};

        when(repository.findById(osId)).thenReturn(Optional.of(os));
        when(templateService.renderServiceOrder(any(ServiceOrderPdfDto.class))).thenReturn(html);
        when(pdfService.gerarPdf(html)).thenReturn(pdfBytes);

        // Act
        byte[] result = serviceOrderPdfService.gerarPdf(osId);

        // Assert
        verify(repository).findById(osId);
        verify(templateService).renderServiceOrder(any(ServiceOrderPdfDto.class));
        verify(pdfService).gerarPdf(html);
        assertArrayEquals(pdfBytes, result);
    }

    @Test
    @DisplayName("Deve lançar exceção se a OS não for encontrada")
    void gerarPdf_ShouldThrowException_WhenOsNotFound() {
        // Arrange
        Long osId = 99L;
        when(repository.findById(osId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> serviceOrderPdfService.gerarPdf(osId));
    }
}