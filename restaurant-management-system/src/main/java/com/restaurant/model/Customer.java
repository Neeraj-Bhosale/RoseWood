package com.restaurant.model;

import java.time.LocalDateTime;

/**
 * Customer domain entity with loyalty tracking.
 */
public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private int loyaltyPoints;
    private int totalVisits;
    private double totalSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Customer() {
        this.loyaltyPoints = 0;
        this.totalVisits = 0;
        this.totalSpent = 0.00;
    }

    public Customer(int customerId, String name, String phone, String email, String address) {
        this();
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public void addSpending(double amount) {
        if (amount > 0) {
            this.totalSpent += amount;
            this.totalVisits += 1;
            // 1 point per 100 spent
            this.loyaltyPoints += (int) (amount / 100);
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(int totalVisits) {
        this.totalVisits = totalVisits;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
