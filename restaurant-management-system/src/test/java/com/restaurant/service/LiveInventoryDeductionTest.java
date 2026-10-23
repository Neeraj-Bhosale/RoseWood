package com.restaurant.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurant.model.InventoryDeductionDetail;
import com.restaurant.model.InventoryItem;
import com.restaurant.model.OrderItem;

public class LiveInventoryDeductionTest {

    private final InventoryService inventoryService = new InventoryService();
    private final DummyDataService dummyDataService = new DummyDataService();

    @Test
    @DisplayName("Verify live inventory reduction and backend deduction audit details")
    public void testLiveOrderDeductionDetails() throws Exception {
        // Ensure dummy inventory and dishes exist
        dummyDataService.importDummySuppliers();
        dummyDataService.importDummyInventory();
        dummyDataService.importDummyDishes();

        // Find initial stock of Fresh Cottage Cheese (Paneer) [ID: 1]
        InventoryItem paneerBefore = inventoryService.getInventoryById(1);
        assertNotNull(paneerBefore, "Paneer inventory item should exist");
        double initialStock = paneerBefore.getQuantity();

        // Order 2 portions of Paneer Butter Masala (menu item 1)
        // Each portion requires 0.25 kg Paneer -> 2 portions = 0.50 kg Paneer
        List<OrderItem> items = List.of(
                new OrderItem(1, "Paneer Butter Masala", 2, 280.0, "Extra butter")
        );

        String testOrderNumber = "ORD-TEST-DEDUCT-" + System.currentTimeMillis();
        List<InventoryDeductionDetail> deductions = inventoryService.deductStockForOrder(testOrderNumber, items);

        assertNotNull(deductions);
        assertFalse(deductions.isEmpty(), "Deductions list should not be empty");

        // Verify Paneer deduction detail
        InventoryDeductionDetail paneerDeduction = deductions.stream()
                .filter(d -> d.getInventoryId() == 1)
                .findFirst()
                .orElse(null);

        assertNotNull(paneerDeduction, "Should contain deduction for Paneer");
        assertEquals(0.50, paneerDeduction.getDeductedQuantity(), 0.001);
        assertEquals(initialStock, paneerDeduction.getPreviousStock(), 0.001);
        assertEquals(initialStock - 0.50, paneerDeduction.getRemainingStock(), 0.001);

        // Verify database updated live
        InventoryItem paneerAfter = inventoryService.getInventoryById(1);
        assertNotNull(paneerAfter);
        assertEquals(initialStock - 0.50, paneerAfter.getQuantity(), 0.001);

        // Restore stock so test is idempotent
        inventoryService.updateStockQuantity(1, initialStock);
    }
}
