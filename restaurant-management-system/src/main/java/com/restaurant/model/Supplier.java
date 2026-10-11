package com.restaurant.model;

import java.time.LocalDateTime;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String suppliedCategory;
    private LocalDateTime createdAt;

    public Supplier() {}

    public Supplier(int supplierId, String name, String contactPerson, String phone, String email, String address, String suppliedCategory) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.suppliedCategory = suppliedCategory;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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

    public String getSuppliedCategory() {
        return suppliedCategory;
    }

    public void setSuppliedCategory(String suppliedCategory) {
        this.suppliedCategory = suppliedCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name + " (" + suppliedCategory + ")";
    }
}
