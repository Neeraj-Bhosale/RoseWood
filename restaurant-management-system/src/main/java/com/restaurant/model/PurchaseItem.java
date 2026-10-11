package com.restaurant.model;

public class PurchaseItem {
    private int purchaseItemId;
    private int purchaseId;
    private int inventoryId;
    private String ingredientName;
    private double quantity;
    private double unitCost;
    private double subtotal;

    public PurchaseItem() {}

    public PurchaseItem(int inventoryId, String ingredientName, double quantity, double unitCost) {
        this.inventoryId = inventoryId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.subtotal = quantity * unitCost;
    }

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.subtotal = quantity * this.unitCost;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
        this.subtotal = this.quantity * unitCost;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
