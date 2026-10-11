package com.restaurant.enums;

public enum PaymentMethod {
    CASH("Cash Settlement"),
    UPI("Unified Payments Interface (UPI)"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
