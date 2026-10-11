package com.restaurant.model;

import com.restaurant.enums.UserRole;
import java.util.List;

public class Cashier extends User {

    public Cashier() {
        this.role = UserRole.CASHIER;
    }

    public Cashier(int userId, String username, String fullName, String email, String phone) {
        super(userId, username, fullName, email, phone, UserRole.CASHIER);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("VIEW_COMPLETED_ORDERS", "GENERATE_BILLS", "APPLY_DISCOUNTS", "PROCESS_PAYMENTS", "PRINT_RECEIPTS", "VIEW_TRANSACTIONS");
    }
}
