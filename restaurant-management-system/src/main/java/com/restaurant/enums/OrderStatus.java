package com.restaurant.enums;

public enum OrderStatus {
    CREATED("Created", 1),
    CONFIRMED("Confirmed", 2),
    PREPARING("Preparing in Kitchen", 3),
    READY("Ready for Service", 4),
    SERVED("Served to Table", 5),
    COMPLETED("Completed & Paid", 6),
    CANCELLED("Cancelled", 0);

    private final String description;
    private final int sequence;

    OrderStatus(String description, int sequence) {
        this.description = description;
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public int getSequence() {
        return sequence;
    }
}
