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
                new OrderItem(5, "Butter Chicken Supreme", 2, 440.0, "Rich gravy"),
                new OrderItem(6, "Paneer Butter Masala", 1, 360.0, "Less oil")
        );

        Map<Integer, Double> deductions = recipeService.buildInventoryDeductionsForOrder(items);

        assertEquals(0.9, deductions.get(3), 0.001);
        assertEquals(0.88, deductions.get(6), 0.001);
        assertEquals(0.40, deductions.get(1), 0.001);
        assertEquals(0.28, deductions.get(12), 0.001);
        assertEquals(0.58, deductions.get(7), 0.001);
    }
}
