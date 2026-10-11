package com.restaurant.service;

import com.restaurant.model.InventoryItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryServiceTest {

    @Test
    @DisplayName("Should correctly detect low-stock threshold")
    public void testLowStockDetection() {
        InventoryItem item = new InventoryItem(1, "Mozzarella Cheese", "Dairy", 3.0, "kg", 5.0, 400.0, 1);
        assertTrue(item.isLowStock(), "Stock is 3.0, minimum is 5.0 -> should be low stock");

        item.setQuantity(10.0);
        assertFalse(item.isLowStock(), "Stock is 10.0, minimum is 5.0 -> should NOT be low stock");
    }

    @Test
    @DisplayName("Should deduct stock safely")
    public void testDeductStock() {
        InventoryItem item = new InventoryItem(1, "Chicken", "Meat", 20.0, "kg", 5.0, 200.0, 1);
        item.deductStock(5.0);
        assertEquals(15.0, item.getQuantity(), 0.001);

        item.deductStock(20.0); // More than available
        assertEquals(0.0, item.getQuantity(), 0.001, "Quantity should not drop below 0");
    }
}
