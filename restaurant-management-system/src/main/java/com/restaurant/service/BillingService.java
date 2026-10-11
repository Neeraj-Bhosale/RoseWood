package com.restaurant.service;

import com.restaurant.config.AppConfig;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.util.ActivityLogger;
import com.restaurant.util.BillReceiptWriter;
import com.restaurant.util.CurrencyUtil;
import com.restaurant.util.DateTimeUtil;

import java.io.IOException;
import java.util.List;

/**
 * Service dedicated to bill calculations, discounts, taxes, and receipt generation.
 */
public class BillingService {

    private final double taxRate;
    private final double serviceChargeRate;

    public BillingService() {
        AppConfig config = AppConfig.getInstance();
        this.taxRate = config.getDouble("app.taxRate", 5.0) / 100.0;
        this.serviceChargeRate = config.getDouble("app.serviceChargeRate", 2.5) / 100.0;
    }

    public BillingService(double taxPercentage, double serviceChargePercentage) {
        this.taxRate = taxPercentage / 100.0;
        this.serviceChargeRate = serviceChargePercentage / 100.0;
    }

    public double calculateSubtotal(List<OrderItem> items) {
        if (items == null) return 0.0;
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public double calculateTax(double taxableAmount) {
        return Math.max(0, taxableAmount) * taxRate;
    }

    public double calculateServiceCharge(double subtotal) {
        return Math.max(0, subtotal) * serviceChargeRate;
    }

    public double calculateGrandTotal(double subtotal, double discountAmount) {
        double discount = Math.min(Math.max(0, discountAmount), subtotal);
        double taxable = subtotal - discount;
        double tax = calculateTax(taxable);
        double service = calculateServiceCharge(subtotal);
        return taxable + tax + service;
    }

    public String generateBillReceipt(Order order) {
        if (order == null) return "Order not found.";
        AppConfig config = AppConfig.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append(String.format("          %s\n", config.getProperty("restaurant.name", "RoseWood Fine Dining")));
        sb.append(String.format("      %s\n", config.getProperty("restaurant.address", "104 Gourmet Blvd")));
        sb.append(String.format("          Tel: %s\n", config.getProperty("restaurant.phone", "+91 98765 43210")));
        sb.append("==================================================\n");
        sb.append(String.format("Bill / Order No : %s\n", order.getOrderNumber()));
        sb.append(String.format("Table           : %s\n", order.getTableNumber() != null ? order.getTableNumber() : "Takeaway / Direct"));
        sb.append(String.format("Customer        : %s\n", order.getCustomerName() != null ? order.getCustomerName() : "Walk-in Guest"));
        sb.append(String.format("Date / Time     : %s\n", DateTimeUtil.formatDateTime(order.getOrderDateTime())));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-25s %5s %9s %8s\n", "Item", "Qty", "Price", "Total"));
        sb.append("--------------------------------------------------\n");

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String name = item.getItemName();
                if (name.length() > 24) name = name.substring(0, 21) + "...";
                sb.append(String.format("%-25s %5d %9.2f %8.2f\n", name, item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));
            }
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-32s : %s\n", "Subtotal", CurrencyUtil.format(order.getSubtotal())));
        if (order.getDiscountAmount() > 0) {
            sb.append(String.format("%-32s : -%s\n", "Discount Applied", CurrencyUtil.format(order.getDiscountAmount())));
        }
        sb.append(String.format("%-32s : %s\n", String.format("GST (%.1f%%)", taxRate * 100), CurrencyUtil.format(order.getTaxAmount())));
        sb.append(String.format("%-32s : %s\n", String.format("Service Charge (%.1f%%)", serviceChargeRate * 100), CurrencyUtil.format(order.getServiceCharge())));
        sb.append("==================================================\n");
        sb.append(String.format("%-32s : %s\n", "GRAND TOTAL", CurrencyUtil.format(order.getTotalAmount())));
        sb.append("==================================================\n");
        sb.append("             THANK YOU! VISIT US AGAIN            \n");
        sb.append("==================================================\n");
        return sb.toString();
    }

    /**
     * FILE HANDLING — saves the generated receipt to a .txt file.
     *
     * Demonstrates IOException handling with try-catch:
     * the caller receives either the saved file path (success)
     * or a fallback message (failure).
     *
     * @param order the completed order
     * @return absolute path of the saved receipt, or an error message string
     */
    public String saveReceiptToFile(Order order) {
        String receiptContent = generateBillReceipt(order);
        BillReceiptWriter writer = new BillReceiptWriter();
        try {
            String savedPath = writer.saveReceipt(order, receiptContent);
            // FILE HANDLING — log the successful save
            ActivityLogger.getInstance().logReceiptSaved(order.getOrderNumber(), savedPath);
            return savedPath;
        } catch (IOException e) {
            // EXCEPTION HANDLING — catch I/O failure; log and return a user-friendly message
            ActivityLogger.getInstance().logError(
                    "BillingService.saveReceiptToFile", e.getMessage());
            return "Receipt could not be saved: " + e.getMessage();
        }
    }
}
