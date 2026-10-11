package com.restaurant.service;

import com.restaurant.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BillingServiceTest {

    private BillingService billingService;

    @BeforeEach
    public void setUp() {
        // 5% tax, 2.5% service charge
        billingService = new BillingService(5.0, 2.5);
    }

    @Test
    @DisplayName("Should correctly calculate line items subtotal")
    public void testCalculateSubtotal() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(1, "Paneer Tikka", 2, 300.0, ""));
        items.add(new OrderItem(2, "Butter Naan", 4, 50.0, ""));

        double subtotal = billingService.calculateSubtotal(items);
        assertEquals(800.0, subtotal, 0.001);
    }

    @Test
    @DisplayName("Should correctly calculate 5% GST on taxable amount")
    public void testCalculateTax() {
        double tax = billingService.calculateTax(1000.0);
        assertEquals(50.0, tax, 0.001);
    }

    @Test
    @DisplayName("Should correctly calculate 2.5% service charge")
    public void testCalculateServiceCharge() {
        double service = billingService.calculateServiceCharge(1000.0);
        assertEquals(25.0, service, 0.001);
    }

    @Test
    @DisplayName("Should compute grand total including discount, tax and service charge")
    public void testCalculateGrandTotalWithDiscount() {
        // Subtotal = 1000, Discount = 100
        // Taxable = 900 -> Tax = 45
        // Service Charge = 2.5% on 1000 = 25
        // Grand Total = 900 + 45 + 25 = 970
        double grandTotal = billingService.calculateGrandTotal(1000.0, 100.0);
        assertEquals(970.0, grandTotal, 0.001);
    }
}
