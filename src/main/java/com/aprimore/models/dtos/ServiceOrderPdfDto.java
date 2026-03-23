package com.aprimore.models.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import com.aprimore.models.Blade;

@Data
public class ServiceOrderPdfDto {

    private Long id;

    private String clientName;
    private String workName;
    private String reference;

    private LocalDate entryDate;
    private LocalDate deliveryDate;

    private String type;
    private String arrangement;
    private String typeOfWave;

    private String servicesToBePerformed;
    private String obs;

    private MachinePdfDto machine;

    private List<Blade> blades;

    @Data
    public static class MachinePdfDto {
        private String name;
        private boolean isRotary;

        // Rotary
        private String centerLine;
        private String diameter;
        private String distanceBetweenHolesInStraightLine;
        private String distanceBetweenHolesInCurvedDirection;
        private String reduction;
        private String totalLengthCylinder;

        // Flat
        private String maxSheetDimension;
    }

}