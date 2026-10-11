package com.restaurant.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Purchase {
    private int purchaseId;
    private int supplierId;
    private String supplierName;
    private LocalDate purchaseDate;
    private double totalCost;
    private String invoiceRef;
    private String notes;
    private List<PurchaseItem> items;

    public Purchase() {
        this.purchaseDate = LocalDate.now();
        this.items = new ArrayList<>();
    }

    public Purchase(int supplierId, LocalDate purchaseDate, String invoiceRef, String notes) {
        this();
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
        this.invoiceRef = invoiceRef;
        this.notes = notes;
    }

    public void recalculateTotal() {
        this.totalCost = items.stream().mapToDouble(PurchaseItem::getSubtotal).sum();
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        recalculateTotal();
    }
}
