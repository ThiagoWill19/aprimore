package com.aprimore.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @InjectMocks
    private PdfService pdfService;

    @Test
    @DisplayName("Deve gerar um PDF a partir de um HTML simples sem lançar exceção")
    void gerarPdf_Success() {
        byte[] result = pdfService.gerarPdf("<html><body><h1>Teste</h1></body></html>");
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}