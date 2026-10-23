package com.restaurant.model;

import java.time.LocalDateTime;

/**
 * Entity model representing a persisted inventory log record in MySQL table `inventory_logs`.
 */
public class InventoryLog {
    private int logId;
    private String orderNumber;
    private int inventoryId;
    private String ingredientName;
    private double deductedQuantity;
    private String unit;
    private double previousStock;
    private double remainingStock;
    private String notes;
    private LocalDateTime createdAt;

    public InventoryLog() {
        this.createdAt = LocalDateTime.now();
    }

    public InventoryLog(int logId, String orderNumber, int inventoryId, String ingredientName,
                        double deductedQuantity, String unit, double previousStock,
                        double remainingStock, String notes, LocalDateTime createdAt) {
        this.logId = logId;
        this.orderNumber = orderNumber;
        this.inventoryId = inventoryId;
        this.ingredientName = ingredientName;
        this.deductedQuantity = deductedQuantity;
        this.unit = unit;
        this.previousStock = previousStock;
        this.remainingStock = remainingStock;
        this.notes = notes;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
