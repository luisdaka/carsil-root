package com.carsil.userapi.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductionStatus {
    PROCESO("PROCESO"),
    ASIGNADO("ASIGNADO"),
    CONFECCION("CONFECCIÓN");

    private final String label;
    ProductionStatus(String label) { this.label = label; }

    public String getLabel() { return label; }

}
