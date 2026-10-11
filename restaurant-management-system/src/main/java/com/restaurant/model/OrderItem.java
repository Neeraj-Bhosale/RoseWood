package com.restaurant.model;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int itemId;
    private String itemName;
    private int quantity;
    private double unitPrice;
    private String specialInstructions;
    private double subtotal;
    private String status;

    public OrderItem() {
        this.quantity = 1;
        this.status = "PENDING";
    }

    public OrderItem(int itemId, String itemName, int quantity, double unitPrice, String specialInstructions) {
        this();
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.specialInstructions = specialInstructions;
        this.subtotal = quantity * unitPrice;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return itemName + " x" + quantity + " (" + subtotal + ")";
    }
}
