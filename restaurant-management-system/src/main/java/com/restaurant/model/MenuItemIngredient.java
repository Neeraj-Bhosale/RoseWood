package com.restaurant.model;

public class MenuItemIngredient {
    private int recipeId;
    private int menuItemId;
    private int inventoryId;
    private String ingredientName;
    private double quantityRequired;
    private String unit;

    public MenuItemIngredient() {
    }

    public MenuItemIngredient(int recipeId, int menuItemId, int inventoryId, String ingredientName,
                             double quantityRequired, String unit) {
        this.recipeId = recipeId;
        this.menuItemId = menuItemId;
        this.inventoryId = inventoryId;
        this.ingredientName = ingredientName;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
