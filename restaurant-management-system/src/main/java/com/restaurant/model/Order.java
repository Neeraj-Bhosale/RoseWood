package com.restaurant.model;

import com.restaurant.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private String orderNumber;
    private Integer customerId;
    private String customerName;
    private Integer tableId;
    private String tableNumber;
    private int employeeId;
    private String employeeName;
    private LocalDateTime orderDateTime;
    private OrderStatus status;
    private double subtotal;
    private double discountAmount;
    private double taxAmount;
    private double serviceCharge;
    private double totalAmount;
    private String specialNotes;
    private List<OrderItem> items;
    private List<InventoryDeductionDetail> inventoryDeductions;

    public Order() {
        this.status = OrderStatus.CREATED;
        this.orderDateTime = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.inventoryDeductions = new ArrayList<>();
    }

    public Order(String orderNumber, Integer customerId, Integer tableId, int employeeId) {
        this();
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.tableId = tableId;
        this.employeeId = employeeId;
    }

    public void addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
            recalculateTotals();
        }
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.subtotal = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
        double taxableAmount = Math.max(0, this.subtotal - this.discountAmount);
        this.taxAmount = taxableAmount * 0.05; // 5% GST
        this.serviceCharge = this.subtotal * 0.025; // 2.5% Service Charge
        this.totalAmount = taxableAmount + this.taxAmount + this.serviceCharge;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        recalculateTotals();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        recalculateTotals();
    }

    public List<InventoryDeductionDetail> getInventoryDeductions() {
        return inventoryDeductions;
    }

    public void setInventoryDeductions(List<InventoryDeductionDetail> inventoryDeductions) {
        this.inventoryDeductions = inventoryDeductions != null ? inventoryDeductions : new ArrayList<>();
    }

    @Override
    public String toString() {
        return orderNumber + " [" + status + "] - Table: " + (tableNumber != null ? tableNumber : "N/A");
    }
}
