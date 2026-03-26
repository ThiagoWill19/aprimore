package com.aprimore.models.dtos;

import lombok.Data;

@Data
public class PcpPdfDto {
    

    private Integer sequencia;
    private long numeroOS;
    private String empresa;
    private String trabalho;

    public PcpPdfDto(Integer sequencia, long numeroOS, String empresa, String trabalho) {
        this.sequencia = sequencia;
        this.numeroOS = numeroOS;
        this.empresa = empresa;
        this.trabalho = trabalho;
    }

}
