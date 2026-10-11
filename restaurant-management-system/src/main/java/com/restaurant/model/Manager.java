package com.restaurant.model;

import com.restaurant.enums.UserRole;
import java.util.List;

public class Manager extends User {

    public Manager() {
        this.role = UserRole.MANAGER;
    }

    public Manager(int userId, String username, String fullName, String email, String phone) {
        super(userId, username, fullName, email, phone, UserRole.MANAGER);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("MANAGE_MENU", "MANAGE_TABLES", "MANAGE_RESERVATIONS", "MANAGE_INVENTORY", "MANAGE_SUPPLIERS", "VIEW_REPORTS", "VIEW_SALES");
    }
}
