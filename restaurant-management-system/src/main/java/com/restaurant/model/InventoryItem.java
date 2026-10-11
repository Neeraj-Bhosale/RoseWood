package com.restaurant.model;

import java.time.LocalDateTime;

public class InventoryItem {
    private int inventoryId;
    private String ingredientName;
    private String category;
    private double quantity;
    private String unit;
    private double minimumStock;
    private double unitCost;
    private Integer supplierId;
    private String supplierName;
    private LocalDateTime lastUpdated;

    public InventoryItem() {
        this.quantity = 0;
        this.minimumStock = 5.0;
        this.unit = "kg";
    }

    public InventoryItem(int inventoryId, String ingredientName, String category, double quantity, String unit, double minimumStock, double unitCost, Integer supplierId) {
        this();
        this.inventoryId = inventoryId;
        this.ingredientName = ingredientName;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.minimumStock = minimumStock;
        this.unitCost = unitCost;
        this.supplierId = supplierId;
    }

    public boolean isLowStock() {
        return this.quantity <= this.minimumStock;
    }

    public void deductStock(double amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    public void addStock(double amount) {
        this.quantity += amount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(double minimumStock) {
        this.minimumStock = minimumStock;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return ingredientName + " (" + quantity + " " + unit + ")";
    }
}
