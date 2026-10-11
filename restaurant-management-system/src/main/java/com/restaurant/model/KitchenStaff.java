package com.restaurant.model;

import com.restaurant.enums.UserRole;
import java.util.List;

public class KitchenStaff extends User {

    public KitchenStaff() {
        this.role = UserRole.KITCHEN_STAFF;
    }

    public KitchenStaff(int userId, String username, String fullName, String email, String phone) {
        super(userId, username, fullName, email, phone, UserRole.KITCHEN_STAFF);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("VIEW_INCOMING_ORDERS", "UPDATE_ORDER_STATUS", "VIEW_ORDER_ITEMS", "MARK_ORDER_READY");
    }
}
