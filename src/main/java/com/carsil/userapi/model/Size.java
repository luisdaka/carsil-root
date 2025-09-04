package com.carsil.userapi.model;

public enum Size {
    T2("2"), T4("4"), T6("6"), T8("8"), T10("10"), T12("12"), T14("14"), T16("16"),
    XS("XS"), S("S"), M("M"), L("L"), XL("XL"), XXL("XXL");

    private final String label;
    Size(String label) { this.label = label; }
    public String getLabel() { return label; }
}

