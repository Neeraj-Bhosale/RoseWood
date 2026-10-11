package com.restaurant.model;

import com.restaurant.enums.UserRole;
import java.util.Arrays;
import java.util.List;

public class Admin extends User {

    public Admin() {
        this.role = UserRole.ADMIN;
    }

    public Admin(int userId, String username, String fullName, String email, String phone) {
        super(userId, username, fullName, email, phone, UserRole.ADMIN);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("ALL", "MANAGE_USERS", "MANAGE_MENU", "MANAGE_TABLES", "MANAGE_INVENTORY", "VIEW_REPORTS", "SYSTEM_SETTINGS");
    }
}
