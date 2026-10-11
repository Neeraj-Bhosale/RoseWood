package com.restaurant.model;

import com.restaurant.enums.UserRole;
import java.util.List;

public class Waiter extends User {

    public Waiter() {
        this.role = UserRole.WAITER;
    }

    public Waiter(int userId, String username, String fullName, String email, String phone) {
        super(userId, username, fullName, email, phone, UserRole.WAITER);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("VIEW_TABLES", "CREATE_ORDER", "MODIFY_ORDER", "VIEW_MENU", "SEND_TO_KITCHEN", "VIEW_ORDER_STATUS");
    }
}
