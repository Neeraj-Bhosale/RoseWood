package com.restaurant.enums;

public enum PaymentStatus {
    PENDING("Payment Pending"),
    PAID("Paid & Settled"),
    FAILED("Payment Failed"),
    REFUNDED("Refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
