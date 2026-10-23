package com.restaurant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.restaurant.model.MenuItemIngredient;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.MenuItemIngredientRepository;

public class MenuRecipeService {
    private static final Logger LOGGER = Logger.getLogger(MenuRecipeService.class.getName());
    private final MenuItemIngredientRepository ingredientRepository;

    private static final Map<Integer, List<MenuItemIngredient>> DEFAULT_RECIPES = new HashMap<>();

    static {
        // 1: Paneer Butter Masala
        DEFAULT_RECIPES.put(1, List.of(
                new MenuItemIngredient(0, 1, 1, "Paneer", 0.25, "kg"),
                new MenuItemIngredient(0, 1, 5, "Butter", 0.05, "kg"),
                new MenuItemIngredient(0, 1, 7, "Tomatoes", 0.20, "kg"),
                new MenuItemIngredient(0, 1, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 1, 6, "Fresh Cream", 0.05, "L"),
                new MenuItemIngredient(0, 1, 16, "Garam Masala", 0.01, "kg")
        ));

        // 2: Kadai Paneer
        DEFAULT_RECIPES.put(2, List.of(
                new MenuItemIngredient(0, 2, 1, "Paneer", 0.25, "kg"),
                new MenuItemIngredient(0, 2, 10, "Capsicum", 0.15, "kg"),
                new MenuItemIngredient(0, 2, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 2, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 2, 18, "Cooking Oil", 0.03, "L"),
                new MenuItemIngredient(0, 2, 16, "Garam Masala", 0.01, "kg")
        ));

        // 3: Palak Paneer
        DEFAULT_RECIPES.put(3, List.of(
                new MenuItemIngredient(0, 3, 1, "Paneer", 0.25, "kg"),
                new MenuItemIngredient(0, 3, 11, "Spinach", 0.30, "kg"),
                new MenuItemIngredient(0, 3, 8, "Onions", 0.10, "kg"),
                new MenuItemIngredient(0, 3, 7, "Tomatoes", 0.10, "kg"),
                new MenuItemIngredient(0, 3, 6, "Fresh Cream", 0.03, "L"),
                new MenuItemIngredient(0, 3, 13, "Garlic", 0.01, "kg")
        ));

        // 4: Dal Tadka
        DEFAULT_RECIPES.put(4, List.of(
                new MenuItemIngredient(0, 4, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 4, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 4, 5, "Butter", 0.03, "kg"),
                new MenuItemIngredient(0, 4, 13, "Garlic", 0.01, "kg"),
                new MenuItemIngredient(0, 4, 17, "Turmeric Powder", 0.005, "kg"),
                new MenuItemIngredient(0, 4, 14, "Green Chilies", 0.01, "kg")
        ));

        // 5: Dal Makhani
        DEFAULT_RECIPES.put(5, List.of(
                new MenuItemIngredient(0, 5, 5, "Butter", 0.08, "kg"),
                new MenuItemIngredient(0, 5, 6, "Fresh Cream", 0.05, "L"),
                new MenuItemIngredient(0, 5, 7, "Tomatoes", 0.20, "kg"),
                new MenuItemIngredient(0, 5, 8, "Onions", 0.10, "kg"),
                new MenuItemIngredient(0, 5, 16, "Garam Masala", 0.01, "kg")
        ));

        // 6: Veg Kolhapuri
        DEFAULT_RECIPES.put(6, List.of(
                new MenuItemIngredient(0, 6, 10, "Capsicum", 0.10, "kg"),
                new MenuItemIngredient(0, 6, 9, "Potatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 6, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 6, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 6, 14, "Green Chilies", 0.02, "kg"),
                new MenuItemIngredient(0, 6, 16, "Garam Masala", 0.01, "kg")
        ));

        // 7: Chole Masala
        DEFAULT_RECIPES.put(7, List.of(
                new MenuItemIngredient(0, 7, 8, "Onions", 0.20, "kg"),
                new MenuItemIngredient(0, 7, 7, "Tomatoes", 0.20, "kg"),
                new MenuItemIngredient(0, 7, 12, "Ginger", 0.02, "kg"),
                new MenuItemIngredient(0, 7, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 7, 16, "Garam Masala", 0.01, "kg"),
                new MenuItemIngredient(0, 7, 18, "Cooking Oil", 0.03, "L")
        ));

        // 8: Aloo Gobi
        DEFAULT_RECIPES.put(8, List.of(
                new MenuItemIngredient(0, 8, 9, "Potatoes", 0.30, "kg"),
                new MenuItemIngredient(0, 8, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 8, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 8, 17, "Turmeric Powder", 0.01, "kg"),
                new MenuItemIngredient(0, 8, 18, "Cooking Oil", 0.03, "L"),
                new MenuItemIngredient(0, 8, 15, "Coriander Leaves", 0.02, "kg")
        ));

        // 9: Butter Chicken
        DEFAULT_RECIPES.put(9, List.of(
                new MenuItemIngredient(0, 9, 2, "Chicken", 0.35, "kg"),
                new MenuItemIngredient(0, 9, 5, "Butter", 0.06, "kg"),
                new MenuItemIngredient(0, 9, 7, "Tomatoes", 0.25, "kg"),
                new MenuItemIngredient(0, 9, 6, "Fresh Cream", 0.05, "L"),
                new MenuItemIngredient(0, 9, 16, "Garam Masala", 0.01, "kg"),
                new MenuItemIngredient(0, 9, 12, "Ginger", 0.01, "kg")
        ));

        // 10: Chicken Curry
        DEFAULT_RECIPES.put(10, List.of(
                new MenuItemIngredient(0, 10, 2, "Chicken", 0.35, "kg"),
                new MenuItemIngredient(0, 10, 8, "Onions", 0.20, "kg"),
                new MenuItemIngredient(0, 10, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 10, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 10, 12, "Ginger", 0.02, "kg"),
                new MenuItemIngredient(0, 10, 18, "Cooking Oil", 0.04, "L")
        ));

        // 11: Chicken Handi
        DEFAULT_RECIPES.put(11, List.of(
                new MenuItemIngredient(0, 11, 2, "Chicken", 0.40, "kg"),
                new MenuItemIngredient(0, 11, 20, "Curd", 0.10, "kg"),
                new MenuItemIngredient(0, 11, 8, "Onions", 0.20, "kg"),
                new MenuItemIngredient(0, 11, 5, "Butter", 0.04, "kg"),
                new MenuItemIngredient(0, 11, 16, "Garam Masala", 0.01, "kg")
        ));

        // 12: Chicken Tikka
        DEFAULT_RECIPES.put(12, List.of(
                new MenuItemIngredient(0, 12, 2, "Chicken", 0.35, "kg"),
                new MenuItemIngredient(0, 12, 20, "Curd", 0.10, "kg"),
                new MenuItemIngredient(0, 12, 12, "Ginger", 0.02, "kg"),
                new MenuItemIngredient(0, 12, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 12, 16, "Garam Masala", 0.01, "kg"),
                new MenuItemIngredient(0, 12, 5, "Butter", 0.03, "kg")
        ));

        // 13: Chicken Biryani
        DEFAULT_RECIPES.put(13, List.of(
                new MenuItemIngredient(0, 13, 2, "Chicken", 0.35, "kg"),
                new MenuItemIngredient(0, 13, 4, "Basmati Rice", 0.30, "kg"),
                new MenuItemIngredient(0, 13, 8, "Onions", 0.20, "kg"),
                new MenuItemIngredient(0, 13, 20, "Curd", 0.08, "kg"),
                new MenuItemIngredient(0, 13, 16, "Garam Masala", 0.01, "kg"),
                new MenuItemIngredient(0, 13, 18, "Cooking Oil", 0.03, "L")
        ));

        // 14: Veg Pulao
        DEFAULT_RECIPES.put(14, List.of(
                new MenuItemIngredient(0, 14, 4, "Basmati Rice", 0.25, "kg"),
                new MenuItemIngredient(0, 14, 10, "Capsicum", 0.08, "kg"),
                new MenuItemIngredient(0, 14, 9, "Potatoes", 0.10, "kg"),
                new MenuItemIngredient(0, 14, 8, "Onions", 0.10, "kg"),
                new MenuItemIngredient(0, 14, 18, "Cooking Oil", 0.03, "L")
        ));

        // 15: Jeera Rice
        DEFAULT_RECIPES.put(15, List.of(
                new MenuItemIngredient(0, 15, 4, "Basmati Rice", 0.25, "kg"),
                new MenuItemIngredient(0, 15, 5, "Butter", 0.03, "kg"),
                new MenuItemIngredient(0, 15, 15, "Coriander Leaves", 0.02, "kg")
        ));

        // 16: Mutton Rogan Josh
        DEFAULT_RECIPES.put(16, List.of(
                new MenuItemIngredient(0, 16, 3, "Mutton", 0.35, "kg"),
                new MenuItemIngredient(0, 16, 20, "Curd", 0.10, "kg"),
                new MenuItemIngredient(0, 16, 8, "Onions", 0.20, "kg"),
                new MenuItemIngredient(0, 16, 12, "Ginger", 0.02, "kg"),
                new MenuItemIngredient(0, 16, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 16, 16, "Garam Masala", 0.015, "kg")
        ));

        // 17: Mutton Keema
        DEFAULT_RECIPES.put(17, List.of(
                new MenuItemIngredient(0, 17, 3, "Mutton", 0.30, "kg"),
                new MenuItemIngredient(0, 17, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 17, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 17, 14, "Green Chilies", 0.02, "kg"),
                new MenuItemIngredient(0, 17, 18, "Cooking Oil", 0.04, "L")
        ));

        // 18: Fish Curry
        DEFAULT_RECIPES.put(18, List.of(
                new MenuItemIngredient(0, 18, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 18, 7, "Tomatoes", 0.20, "kg"),
                new MenuItemIngredient(0, 18, 12, "Ginger", 0.02, "kg"),
                new MenuItemIngredient(0, 18, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 18, 17, "Turmeric Powder", 0.01, "kg"),
                new MenuItemIngredient(0, 18, 18, "Cooking Oil", 0.04, "L")
        ));

        // 19: Butter Naan
        DEFAULT_RECIPES.put(19, List.of(
                new MenuItemIngredient(0, 19, 19, "Wheat Flour", 0.15, "kg"),
                new MenuItemIngredient(0, 19, 5, "Butter", 0.03, "kg"),
                new MenuItemIngredient(0, 19, 20, "Curd", 0.03, "kg")
        ));

        // 20: Garlic Naan
        DEFAULT_RECIPES.put(20, List.of(
                new MenuItemIngredient(0, 20, 19, "Wheat Flour", 0.15, "kg"),
                new MenuItemIngredient(0, 20, 13, "Garlic", 0.03, "kg"),
                new MenuItemIngredient(0, 20, 5, "Butter", 0.03, "kg"),
                new MenuItemIngredient(0, 20, 15, "Coriander Leaves", 0.01, "kg")
        ));

        // 21: Tandoori Roti
        DEFAULT_RECIPES.put(21, List.of(
                new MenuItemIngredient(0, 21, 19, "Wheat Flour", 0.12, "kg")
        ));

        // 22: Veg Manchurian
        DEFAULT_RECIPES.put(22, List.of(
                new MenuItemIngredient(0, 22, 10, "Capsicum", 0.10, "kg"),
                new MenuItemIngredient(0, 22, 8, "Onions", 0.10, "kg"),
                new MenuItemIngredient(0, 22, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 22, 12, "Ginger", 0.02, "kg"),
                new MenuItemIngredient(0, 22, 18, "Cooking Oil", 0.04, "L")
        ));

        // 23: Hakka Noodles
        DEFAULT_RECIPES.put(23, List.of(
                new MenuItemIngredient(0, 23, 10, "Capsicum", 0.10, "kg"),
                new MenuItemIngredient(0, 23, 8, "Onions", 0.10, "kg"),
                new MenuItemIngredient(0, 23, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 23, 18, "Cooking Oil", 0.03, "L")
        ));

        // 24: Masala Dosa
        DEFAULT_RECIPES.put(24, List.of(
                new MenuItemIngredient(0, 24, 9, "Potatoes", 0.18, "kg"),
                new MenuItemIngredient(0, 24, 8, "Onions", 0.08, "kg"),
                new MenuItemIngredient(0, 24, 5, "Butter", 0.02, "kg"),
                new MenuItemIngredient(0, 24, 14, "Green Chilies", 0.01, "kg")
        ));

        // 25: Idli Sambar
        DEFAULT_RECIPES.put(25, List.of(
                new MenuItemIngredient(0, 25, 7, "Tomatoes", 0.10, "kg"),
                new MenuItemIngredient(0, 25, 8, "Onions", 0.08, "kg"),
                new MenuItemIngredient(0, 25, 17, "Turmeric Powder", 0.005, "kg"),
                new MenuItemIngredient(0, 25, 15, "Coriander Leaves", 0.01, "kg")
        ));

        // 26: Pav Bhaji
        DEFAULT_RECIPES.put(26, List.of(
                new MenuItemIngredient(0, 26, 9, "Potatoes", 0.20, "kg"),
                new MenuItemIngredient(0, 26, 7, "Tomatoes", 0.15, "kg"),
                new MenuItemIngredient(0, 26, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 26, 10, "Capsicum", 0.08, "kg"),
                new MenuItemIngredient(0, 26, 5, "Butter", 0.05, "kg")
        ));

        // 27: Misal Pav
        DEFAULT_RECIPES.put(27, List.of(
                new MenuItemIngredient(0, 27, 8, "Onions", 0.15, "kg"),
                new MenuItemIngredient(0, 27, 7, "Tomatoes", 0.10, "kg"),
                new MenuItemIngredient(0, 27, 12, "Ginger", 0.01, "kg"),
                new MenuItemIngredient(0, 27, 13, "Garlic", 0.01, "kg"),
                new MenuItemIngredient(0, 27, 18, "Cooking Oil", 0.03, "L")
        ));

        // 28: Gulab Jamun
        DEFAULT_RECIPES.put(28, List.of(
                new MenuItemIngredient(0, 28, 1, "Paneer", 0.10, "kg"),
                new MenuItemIngredient(0, 28, 19, "Wheat Flour", 0.05, "kg"),
                new MenuItemIngredient(0, 28, 5, "Butter", 0.03, "kg")
        ));

        // 29: Rasmalai
        DEFAULT_RECIPES.put(29, List.of(
                new MenuItemIngredient(0, 29, 1, "Paneer", 0.15, "kg"),
                new MenuItemIngredient(0, 29, 6, "Fresh Cream", 0.05, "L")
        ));

        // 30: Mango Lassi
        DEFAULT_RECIPES.put(30, List.of(
                new MenuItemIngredient(0, 30, 20, "Curd", 0.20, "kg"),
                new MenuItemIngredient(0, 30, 6, "Fresh Cream", 0.03, "L")
        ));

        // 31: Masala Chai
        DEFAULT_RECIPES.put(31, List.of(
                new MenuItemIngredient(0, 31, 6, "Fresh Cream", 0.05, "L"),
                new MenuItemIngredient(0, 31, 12, "Ginger", 0.01, "kg")
        ));

        // 32: Veg Hakka Fried Rice
        DEFAULT_RECIPES.put(32, List.of(
                new MenuItemIngredient(0, 32, 4, "Basmati Rice", 0.25, "kg"),
                new MenuItemIngredient(0, 32, 10, "Capsicum", 0.08, "kg"),
                new MenuItemIngredient(0, 32, 8, "Onions", 0.08, "kg"),
                new MenuItemIngredient(0, 32, 13, "Garlic", 0.02, "kg"),
                new MenuItemIngredient(0, 32, 18, "Cooking Oil", 0.03, "L")
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

    public boolean addIngredientToDish(int menuItemId, int inventoryId, double quantity, String unit) {
        MenuItemIngredient mii = new MenuItemIngredient();
        mii.setMenuItemId(menuItemId);
        mii.setInventoryId(inventoryId);
        mii.setQuantityRequired(quantity);
        mii.setUnit(unit != null ? unit : "kg");
        return ingredientRepository.create(mii);
    }

    public boolean removeIngredient(int recipeId) {
        return ingredientRepository.delete(recipeId);
    }

    public boolean removeAllIngredientsForDish(int menuItemId) {
        return ingredientRepository.deleteByMenuItemId(menuItemId);
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

    public static Map<Integer, List<MenuItemIngredient>> getDefaultRecipes() {
        return DEFAULT_RECIPES;
    }
}
