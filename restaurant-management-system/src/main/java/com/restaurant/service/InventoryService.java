package com.restaurant.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.restaurant.data.SearchSortUtil;
import com.restaurant.exception.InsufficientStockException;
import com.restaurant.model.InventoryDeductionDetail;
import com.restaurant.model.InventoryItem;
import com.restaurant.model.InventoryLog;
import com.restaurant.model.MenuItemIngredient;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.InventoryLogRepository;
import com.restaurant.repository.InventoryRepository;
import com.restaurant.util.ActivityLogger;
import com.restaurant.util.ValidationUtil;

public class InventoryService {
    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final MenuRecipeService menuRecipeService;

    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
        this.inventoryLogRepository = new InventoryLogRepository();
        this.menuRecipeService = new MenuRecipeService();
    }

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = new InventoryLogRepository();
        this.menuRecipeService = new MenuRecipeService();
    }

    public InventoryService(InventoryRepository inventoryRepository, MenuRecipeService menuRecipeService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = new InventoryLogRepository();
        this.menuRecipeService = menuRecipeService;
    }

    public List<InventoryItem> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public InventoryItem getInventoryById(int id) {
        return inventoryRepository.findById(id);
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

    /**
     * Live deduction of inventory when an order is placed.
     * Computes required ingredients for each dish, validates stock,
     * updates stock in MySQL, writes audit log rows to `inventory_logs`,
     * appends to activity.log, prints detailed backend audit to console,
     * and returns the exact list of deductions.
     */
    public List<InventoryDeductionDetail> deductStockForOrder(String orderNumber, List<OrderItem> orderItems)
            throws InsufficientStockException {
        return deductStockForOrder(orderNumber, orderItems, null);
    }

    public List<InventoryDeductionDetail> deductStockForOrder(String orderNumber, List<OrderItem> orderItems,
                                                             Map<Integer, Double> customDeductions)
            throws InsufficientStockException {
        List<InventoryDeductionDetail> deductionDetails = new ArrayList<>();
        if ((orderItems == null || orderItems.isEmpty()) && (customDeductions == null || customDeductions.isEmpty())) {
            return deductionDetails;
        }

        // 1. Calculate required quantities per inventory item from dishes
        Map<Integer, Double> aggregatedRequired = new HashMap<>();
        Map<Integer, List<OrderItem>> inventoryToItems = new HashMap<>();

        if (orderItems != null) {
            for (OrderItem orderItem : orderItems) {
                if (orderItem == null || orderItem.getItemId() <= 0) continue;
                List<MenuItemIngredient> ingredients = menuRecipeService.getIngredientsForMenuItem(orderItem.getItemId());
                for (MenuItemIngredient ing : ingredients) {
                    double req = ing.getQuantityRequired() * orderItem.getQuantity();
                    aggregatedRequired.merge(ing.getInventoryId(), req, Double::sum);
                    inventoryToItems.computeIfAbsent(ing.getInventoryId(), k -> new ArrayList<>()).add(orderItem);
                }
            }
        }

        // Apply custom deductions if provided (e.g. from tests or overrides)
        if (customDeductions != null) {
            customDeductions.forEach((id, qty) -> aggregatedRequired.merge(id, qty, Double::sum));
        }

        if (aggregatedRequired.isEmpty()) {
            return deductionDetails;
        }

        // 2. Fetch current inventory stock from DB
        List<InventoryItem> currentInventory = getAllInventory();
        Map<Integer, InventoryItem> inventoryMap = new HashMap<>();
        for (InventoryItem inv : currentInventory) {
            inventoryMap.put(inv.getInventoryId(), inv);
        }

        // 3. Pre-validate stock availability for all required ingredients
        for (Map.Entry<Integer, Double> entry : aggregatedRequired.entrySet()) {
            int invId = entry.getKey();
            double needed = entry.getValue();
            InventoryItem stockItem = inventoryMap.get(invId);

            if (stockItem == null) {
                throw new InsufficientStockException("Inventory ingredient with ID " + invId + " was not found in stock.");
            }
            if (stockItem.getQuantity() < needed) {
                throw new InsufficientStockException(String.format(
                        "Insufficient stock for '%s'. Required: %.2f %s, Available: %.2f %s",
                        stockItem.getIngredientName(), needed, stockItem.getUnit(), stockItem.getQuantity(), stockItem.getUnit()));
            }
        }

        // 4. Perform live database deductions & build audit records
        for (Map.Entry<Integer, Double> entry : aggregatedRequired.entrySet()) {
            int invId = entry.getKey();
            double deductedQty = entry.getValue();
            InventoryItem stockItem = inventoryMap.get(invId);

            double prevStock = stockItem.getQuantity();
            double newStock = prevStock - deductedQty;

            // Update in MySQL database live
            inventoryRepository.updateStock(invId, newStock);
            stockItem.setQuantity(newStock); // update local ref

            // Associate with dish info for audit
            List<OrderItem> dishes = inventoryToItems.get(invId);
            String dishSummary = "";
            int dishQty = 0;
            int dishItemId = 0;
            if (dishes != null && !dishes.isEmpty()) {
                OrderItem firstDish = dishes.get(0);
                dishSummary = firstDish.getItemName();
                dishQty = firstDish.getQuantity();
                dishItemId = firstDish.getItemId();
            }

            InventoryDeductionDetail detail = new InventoryDeductionDetail(
                    orderNumber != null ? orderNumber : "ORD-LIVE",
                    dishItemId,
                    dishSummary,
                    dishQty,
                    invId,
                    stockItem.getIngredientName(),
                    deductedQty,
                    stockItem.getUnit(),
                    prevStock,
                    newStock
            );
            deductionDetails.add(detail);
        }

        // 5. Backend Logging & Audit Persistence
        printBackendDeductionAudit(orderNumber, deductionDetails);
        ActivityLogger.getInstance().logInventoryDeductions(orderNumber, deductionDetails);
        try {
            inventoryLogRepository.saveBatch(deductionDetails);
        } catch (Exception e) {
            LOGGER.warning("Could not persist deduction logs to inventory_logs table: " + e.getMessage());
        }

        return deductionDetails;
    }

    /** Legacy method maintained for unit test compatibility */
    public void deductStockForOrder(Map<Integer, Double> requestedStock) throws InsufficientStockException {
        deductStockForOrder("ORD-LEGACY", null, requestedStock);
    }

    private void printBackendDeductionAudit(String orderNumber, List<InventoryDeductionDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================================================================\n");
        sb.append(String.format(" [LIVE INVENTORY DEDUCTION - BACKEND AUDIT]\n"));
        sb.append(String.format(" Order Number : %s\n", orderNumber));
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(" Exact Ingredient Deductions from MySQL Inventory:\n");
        for (InventoryDeductionDetail d : details) {
            sb.append(String.format("   • %-32s : -%.2f %-4s | Stock: %.2f -> %.2f %s\n",
                    d.getIngredientName(), d.getDeductedQuantity(), d.getUnit(),
                    d.getPreviousStock(), d.getRemainingStock(), d.getUnit()));
        }
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format(" Total Ingredients Reduced: %d | Stock updated live in database.\n", details.size()));
        sb.append("================================================================================\n");
        LOGGER.info(sb.toString());
        System.out.println(sb.toString());
    }

    public List<InventoryLog> getInventoryLogs(int limit) {
        return inventoryLogRepository.findAll(limit);
    }

    public boolean updateInventoryItem(InventoryItem item) {
        if (item == null || item.getInventoryId() <= 0) return false;
        return inventoryRepository.update(item);
    }

    public boolean deleteInventoryItem(int inventoryId) {
        return inventoryRepository.delete(inventoryId);
    }

    public boolean deleteAllInventory() {
        return inventoryRepository.deleteAll();
    }

    public List<InventoryItem> getInventorySortedByStockAscending() {
        List<InventoryItem> list = getAllInventory();
        SearchSortUtil.quickSortInventoryByQuantity(list);
        return list;
    }
}
