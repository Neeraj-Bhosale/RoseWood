package com.restaurant.enums;

public enum TableStatus {
    AVAILABLE("Available", "#2ECC71"),
    OCCUPIED("Occupied", "#E74C3C"),
    RESERVED("Reserved", "#F39C12"),
    CLEANING("Cleaning", "#3498DB");

    private final String label;
    private final String colorHex;

    TableStatus(String label, String colorHex) {
        this.label = label;
        this.colorHex = colorHex;
    }

    public String getLabel() {
        return label;
    }

    public String getColorHex() {
        return colorHex;
    }
}
