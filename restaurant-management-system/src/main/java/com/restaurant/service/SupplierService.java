package com.restaurant.service;

import com.restaurant.model.Supplier;
import com.restaurant.repository.SupplierRepository;
import com.restaurant.util.ValidationUtil;

import java.util.List;

public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService() {
        this.supplierRepository = new SupplierRepository();
    }

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(int supplierId) {
        return supplierRepository.findById(supplierId);
    }

    public boolean addSupplier(String name, String contactPerson, String phone, String email, String address, String suppliedCategory) {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Supplier company name is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Valid contact phone number is required.");
        }
        Supplier s = new Supplier(0, name.trim(), contactPerson, phone.trim(), email, address, suppliedCategory);
        return supplierRepository.create(s);
    }

    public boolean updateSupplier(Supplier s) {
        if (s == null || s.getSupplierId() <= 0) return false;
        return supplierRepository.update(s);
    }

    public boolean deleteSupplier(int supplierId) {
        return supplierRepository.delete(supplierId);
    }
}
