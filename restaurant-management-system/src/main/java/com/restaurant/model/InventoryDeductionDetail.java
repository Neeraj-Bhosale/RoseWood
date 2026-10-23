package com.restaurant.model;

import java.time.LocalDateTime;

/**
 * Model representing an exact inventory deduction record for a specific order.
 * Tracks the menu item, ingredient, deducted quantity, and stock before/after.
 */
public class InventoryDeductionDetail {
    private String orderNumber;
    private int menuItemId;
    private String menuItemName;
    private int orderQuantity;
    private int inventoryId;
    private String ingredientName;
    private double deductedQuantity;
    private String unit;
    private double previousStock;
    private double remainingStock;
    private LocalDateTime timestamp;

    public InventoryDeductionDetail() {
        this.timestamp = LocalDateTime.now();
    }

    public InventoryDeductionDetail(String orderNumber, int menuItemId, String menuItemName, int orderQuantity,
                                    int inventoryId, String ingredientName, double deductedQuantity,
                                    String unit, double previousStock, double remainingStock) {
        this.orderNumber = orderNumber;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.orderQuantity = orderQuantity;
        this.inventoryId = inventoryId;
        this.ingredientName = ingredientName;
        this.deductedQuantity = deductedQuantity;
        this.unit = unit;
        this.previousStock = previousStock;
        this.remainingStock = remainingStock;
        this.timestamp = LocalDateTime.now();
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
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

    public double getDeductedQuantity() {
        return deductedQuantity;
    }

    public void setDeductedQuantity(double deductedQuantity) {
        this.deductedQuantity = deductedQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(double previousStock) {
        this.previousStock = previousStock;
    }

    public double getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(double remainingStock) {
        this.remainingStock = remainingStock;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: Deducted %.2f %s (Was: %.2f %s -> Remaining: %.2f %s)",
                orderNumber, ingredientName, deductedQuantity, unit, previousStock, unit, remainingStock, unit);
    }
}
