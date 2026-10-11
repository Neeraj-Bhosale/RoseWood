package com.restaurant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restaurant.data.SearchSortUtil;
import com.restaurant.exception.InsufficientStockException;
import com.restaurant.model.InventoryItem;
import com.restaurant.repository.InventoryRepository;
import com.restaurant.util.ValidationUtil;

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
    }

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryItem> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public boolean addInventoryItem(String name, String category, double quantity, String unit, double minStock, double unitCost, Integer supplierId) {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Ingredient name is required.");
        }
        if (!ValidationUtil.isNonNegative(quantity) || !ValidationUtil.isNonNegative(minStock)) {
            throw new IllegalArgumentException("Quantity and minimum stock cannot be negative.");
        }
        InventoryItem item = new InventoryItem(0, name.trim(), category, quantity, unit, minStock, unitCost, supplierId);
        return inventoryRepository.create(item);
    }

    public boolean updateStockQuantity(int inventoryId, double newQuantity) {
        if (!ValidationUtil.isNonNegative(newQuantity)) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        return inventoryRepository.updateStock(inventoryId, newQuantity);
    }

    public void deductStock(int inventoryId, double amountToDeduct) throws InsufficientStockException {
        List<InventoryItem> items = getAllInventory();
        InventoryItem target = null;
        for (InventoryItem item : items) {
            if (item.getInventoryId() == inventoryId) {
                target = item;
                break;
            }
        }
        if (target != null) {
            if (target.getQuantity() < amountToDeduct) {
                throw new InsufficientStockException("Insufficient stock for " + target.getIngredientName() +
                        ". Available: " + target.getQuantity() + " " + target.getUnit() + ", Requested: " + amountToDeduct);
            }
            inventoryRepository.updateStock(inventoryId, target.getQuantity() - amountToDeduct);
        }
    }

    public void deductStockForOrder(Map<Integer, Double> requestedStock) throws InsufficientStockException {
        if (requestedStock == null || requestedStock.isEmpty()) {
            return;
        }

        Map<Integer, Double> remainingDeductions = new HashMap<>(requestedStock);
        List<InventoryItem> inventoryItems = getAllInventory();

        for (InventoryItem item : inventoryItems) {
            Double amountToDeduct = remainingDeductions.get(item.getInventoryId());
            if (amountToDeduct == null) {
                continue;
            }
            if (item.getQuantity() < amountToDeduct) {
                throw new InsufficientStockException("Insufficient stock for " + item.getIngredientName() +
                        ". Available: " + item.getQuantity() + " " + item.getUnit() + ", Requested: " + amountToDeduct);
            }
            inventoryRepository.updateStock(item.getInventoryId(), item.getQuantity() - amountToDeduct);
            remainingDeductions.remove(item.getInventoryId());
        }

        if (!remainingDeductions.isEmpty()) {
            for (Integer missingInventoryId : remainingDeductions.keySet()) {
                throw new InsufficientStockException("Inventory item ID " + missingInventoryId + " was not found.");
            }
        }
    }

    public boolean updateInventoryItem(InventoryItem item) {
        if (item == null || item.getInventoryId() <= 0) return false;
        return inventoryRepository.update(item);
    }

    public boolean deleteInventoryItem(int inventoryId) {
        return inventoryRepository.delete(inventoryId);
    }

    public List<InventoryItem> getInventorySortedByStockAscending() {
        List<InventoryItem> list = getAllInventory();
        SearchSortUtil.quickSortInventoryByQuantity(list);
        return list;
    }
}
