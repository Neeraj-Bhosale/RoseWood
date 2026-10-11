package com.restaurant.service;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    @Test
    @DisplayName("Order subtotal and totals recalculation when adding items")
    public void testOrderRecalculation() {
        Order order = new Order("ORD-TEST-001", 1, 2, 3);
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(1, "Chicken Biryani", 2, 350.0, "Extra spicy"));
        items.add(new OrderItem(2, "Cold Coffee", 1, 150.0, "No sugar"));

        order.setItems(items);

        // Subtotal = (2*350) + (1*150) = 850.0
        assertEquals(850.0, order.getSubtotal(), 0.001);

        // Tax (5%) = 42.5
        assertEquals(42.5, order.getTaxAmount(), 0.001);

        // Service Charge (2.5%) = 21.25
        assertEquals(21.25, order.getServiceCharge(), 0.001);

        // Grand Total = 850 + 42.5 + 21.25 = 913.75
        assertEquals(913.75, order.getTotalAmount(), 0.001);
    }
}
