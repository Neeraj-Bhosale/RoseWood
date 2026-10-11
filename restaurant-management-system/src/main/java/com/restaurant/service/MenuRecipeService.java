package com.restaurant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restaurant.model.MenuItemIngredient;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.MenuItemIngredientRepository;

public class MenuRecipeService {
    private final MenuItemIngredientRepository ingredientRepository;

    private static final Map<Integer, List<MenuItemIngredient>> DEFAULT_RECIPES = new HashMap<>();

    static {
        DEFAULT_RECIPES.put(1, List.of(
                new MenuItemIngredient(0, 1, 1, "Fresh Cottage Cheese (Paneer)", 0.35, "kg"),
                new MenuItemIngredient(0, 1, 8, "Bell Peppers (Capsicum)", 0.20, "kg"),
                new MenuItemIngredient(0, 1, 7, "Onions", 0.10, "kg")
        ));

        DEFAULT_RECIPES.put(5, List.of(
                new MenuItemIngredient(0, 5, 3, "Fresh Chicken Breast", 0.45, "kg"),
                new MenuItemIngredient(0, 5, 6, "Tomatoes", 0.30, "kg"),
                new MenuItemIngredient(0, 5, 7, "Onions", 0.20, "kg"),
                new MenuItemIngredient(0, 5, 12, "Cooking Butter", 0.10, "kg")
        ));

        DEFAULT_RECIPES.put(6, List.of(
                new MenuItemIngredient(0, 6, 1, "Fresh Cottage Cheese (Paneer)", 0.40, "kg"),
                new MenuItemIngredient(0, 6, 6, "Tomatoes", 0.28, "kg"),
                new MenuItemIngredient(0, 6, 7, "Onions", 0.18, "kg"),
                new MenuItemIngredient(0, 6, 12, "Cooking Butter", 0.08, "kg")
        ));

        DEFAULT_RECIPES.put(7, List.of(
                new MenuItemIngredient(0, 7, 9, "Basmati Rice Premium", 0.25, "kg"),
                new MenuItemIngredient(0, 7, 7, "Onions", 0.12, "kg"),
                new MenuItemIngredient(0, 7, 12, "Cooking Butter", 0.10, "kg")
        ));

        DEFAULT_RECIPES.put(8, List.of(
                new MenuItemIngredient(0, 8, 4, "Mutton Cuts", 0.60, "kg"),
                new MenuItemIngredient(0, 8, 9, "Basmati Rice Premium", 0.45, "kg"),
                new MenuItemIngredient(0, 8, 6, "Tomatoes", 0.30, "kg"),
                new MenuItemIngredient(0, 8, 7, "Onions", 0.20, "kg")
        ));

        DEFAULT_RECIPES.put(10, List.of(
                new MenuItemIngredient(0, 10, 2, "Mozzarella Cheese", 0.25, "kg"),
                new MenuItemIngredient(0, 10, 6, "Tomatoes", 0.30, "kg"),
                new MenuItemIngredient(0, 10, 7, "Onions", 0.10, "kg")
        ));

        DEFAULT_RECIPES.put(11, List.of(
                new MenuItemIngredient(0, 11, 2, "Mozzarella Cheese", 0.18, "kg"),
                new MenuItemIngredient(0, 11, 12, "Cooking Butter", 0.10, "kg"),
                new MenuItemIngredient(0, 11, 7, "Onions", 0.12, "kg")
        ));

        DEFAULT_RECIPES.put(12, List.of(
                new MenuItemIngredient(0, 12, 2, "Mozzarella Cheese", 0.22, "kg"),
                new MenuItemIngredient(0, 12, 6, "Tomatoes", 0.25, "kg"),
                new MenuItemIngredient(0, 12, 7, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 12, 10, "All-Purpose Flour (Maida)", 0.15, "kg")
        ));

        DEFAULT_RECIPES.put(13, List.of(
                new MenuItemIngredient(0, 13, 3, "Fresh Chicken Breast", 0.25, "kg"),
                new MenuItemIngredient(0, 13, 10, "All-Purpose Flour (Maida)", 0.10, "kg"),
                new MenuItemIngredient(0, 13, 7, "Onions", 0.12, "kg")
        ));
    }

    public MenuRecipeService() {
        this.ingredientRepository = new MenuItemIngredientRepository();
    }

    public MenuRecipeService(MenuItemIngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<MenuItemIngredient> getIngredientsForMenuItem(int menuItemId) {
        List<MenuItemIngredient> ingredients = ingredientRepository.findByMenuItemId(menuItemId);
        if (ingredients != null && !ingredients.isEmpty()) {
            return ingredients;
        }
        return DEFAULT_RECIPES.getOrDefault(menuItemId, List.of());
    }

    public Map<Integer, Double> buildInventoryDeductionsForOrder(List<OrderItem> orderItems) {
        Map<Integer, Double> deductions = new HashMap<>();
        if (orderItems == null || orderItems.isEmpty()) {
            return deductions;
        }

        for (OrderItem item : orderItems) {
            if (item == null || item.getItemId() <= 0) {
                continue;
            }
            List<MenuItemIngredient> ingredients = getIngredientsForMenuItem(item.getItemId());
            for (MenuItemIngredient ingredient : ingredients) {
                double totalRequired = ingredient.getQuantityRequired() * item.getQuantity();
                deductions.merge(ingredient.getInventoryId(), totalRequired, Double::sum);
            }
        }

        return deductions;
    }
}
