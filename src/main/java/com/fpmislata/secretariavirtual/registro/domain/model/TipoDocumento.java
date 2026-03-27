package com.fpmislata.secretariavirtual.registro.domain.model;

public enum TipoDocumento {

    DNI_NIE("DNI / NIE"),
    OTRO("Otro documento");

    private final String label;

    TipoDocumento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
