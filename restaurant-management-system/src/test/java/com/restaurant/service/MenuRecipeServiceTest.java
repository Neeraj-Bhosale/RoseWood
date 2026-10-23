package com.restaurant.service;

import com.restaurant.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuRecipeServiceTest {

    @Test
    @DisplayName("Recipe service should map menu items to inventory deduction totals")
    public void shouldBuildInventoryDeductionMapFromOrderItems() {
        MenuRecipeService recipeService = new MenuRecipeService();

        List<OrderItem> items = List.of(
                new OrderItem(9, "Butter Chicken", 2, 340.0, "Rich gravy"),
                new OrderItem(1, "Paneer Butter Masala", 1, 280.0, "Less oil")
        );

        Map<Integer, Double> deductions = recipeService.buildInventoryDeductionsForOrder(items);

        assertEquals(0.25, deductions.get(1), 0.001);  // Paneer
        assertEquals(0.70, deductions.get(2), 0.001);  // Chicken
        assertEquals(0.17, deductions.get(5), 0.001);  // Butter
        assertEquals(0.15, deductions.get(6), 0.001);  // Fresh Cream
        assertEquals(0.70, deductions.get(7), 0.001);  // Tomatoes
        assertEquals(0.15, deductions.get(8), 0.001);  // Onions
        assertEquals(0.03, deductions.get(16), 0.001); // Garam Masala
    }
}
