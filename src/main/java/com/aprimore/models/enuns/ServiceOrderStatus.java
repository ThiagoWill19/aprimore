package com.aprimore.models.enuns;

public enum ServiceOrderStatus {
    OPEN(0, "ABERTA"),
    CLOSED(2, "FECHADA"),
    CANCELED(1, "CANCELADA");

    private Integer code;
    private String description;

    ServiceOrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
}
