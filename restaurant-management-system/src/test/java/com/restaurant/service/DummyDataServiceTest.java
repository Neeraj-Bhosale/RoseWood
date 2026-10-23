package com.restaurant.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DummyDataServiceTest {

    private final DummyDataService dummyDataService = new DummyDataService();

    @Test
    @DisplayName("Verify DummyDataService instance and import helpers")
    public void testDummyDataServiceBasics() {
        assertNotNull(dummyDataService);
        // Ensure dummy import methods execute without exception
        int tables = dummyDataService.importDummyTables();
        assertTrue(tables >= 0);

        int suppliers = dummyDataService.importDummySuppliers();
        assertTrue(suppliers >= 0);

        int inventory = dummyDataService.importDummyInventory();
        assertTrue(inventory >= 0);

        int dishes = dummyDataService.importDummyDishes();
        assertTrue(dishes >= 0);

        int customers = dummyDataService.importDummyCustomers();
        assertTrue(customers >= 0);
    }
}
