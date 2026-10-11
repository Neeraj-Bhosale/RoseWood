package com.restaurant.enums;

public enum UserRole {
    ADMIN("Administrator", "Full system access"),
    MANAGER("Manager", "Management of menu, tables, inventory and analytics"),
    WAITER("Waiter / Server", "Order creation, table seating and kitchen dispatch"),
    CASHIER("Cashier", "Billing calculation, receipts and payment processing"),
    KITCHEN_STAFF("Kitchen Staff", "Live kitchen display and order status updates");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
