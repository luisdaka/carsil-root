package com.carsil.userapi.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum StoppageReason {
    MARQUILLA_TALLA("MARQUILLA TALLA"),
    COMPOSICION("COMPOSICION"),
    CODIGO("CODIGO"),
    FALTANTE_DE_PIEZA("FALTANTE DE PIEZA"),
    BOLSAS("BOLSAS"),
    FALTA_TODO("FALTA TODO"),
    OK("OK"),
    FICHA("FICHA"),
    SESGO("SESGO");

    private final String label;
    StoppageReason(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static StoppageReason fromJson(String value) {
        if (value == null) return null;
        String norm = value.trim();
        return Arrays.stream(values())
                .filter(r -> r.label.equalsIgnoreCase(norm))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Descripción Paro: " + value));
    }

    public static StoppageReason fromLabel(String label) { return fromJson(label); }
}

