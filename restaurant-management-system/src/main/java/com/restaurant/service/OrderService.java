package com.restaurant.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.restaurant.data.DataStructureManager;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.TableStatus;
import com.restaurant.exception.InsufficientStockException;
import com.restaurant.exception.InvalidOrderException;
import com.restaurant.exception.MenuItemNotAvailableException;
import com.restaurant.exception.TableNotAvailableException;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.TableRepository;
import com.restaurant.util.ActivityLogger;

public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final MenuService menuService;
    private final InventoryService inventoryService;
    private final MenuRecipeService menuRecipeService;

    public OrderService() {
        this.orderRepository = new OrderRepository();
        this.tableRepository = new TableRepository();
        this.menuService = new MenuService();
        this.inventoryService = new InventoryService();
        this.menuRecipeService = new MenuRecipeService();
    }

    public OrderService(OrderRepository orderRepository, TableRepository tableRepository, MenuService menuService) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.menuService = menuService;
        this.inventoryService = new InventoryService();
        this.menuRecipeService = new MenuRecipeService();
    }

    public Order createNewOrder(Integer customerId, Integer tableId, int employeeId, List<OrderItem> items, String specialNotes)
            throws InvalidOrderException, TableNotAvailableException, MenuItemNotAvailableException, InsufficientStockException {
        return createNewOrder(customerId, tableId, employeeId, items, specialNotes, null);
    }

    public Order createNewOrder(Integer customerId, Integer tableId, int employeeId, List<OrderItem> items, String specialNotes,
                               Map<Integer, Double> inventoryDeductions)
            throws InvalidOrderException, TableNotAvailableException, MenuItemNotAvailableException, InsufficientStockException {

        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one menu item.");
        }

        if (tableId != null && tableId > 0) {
            var table = tableRepository.findById(tableId);
            if (table != null && table.getStatus() == TableStatus.OCCUPIED) {
                throw new TableNotAvailableException("Table " + table.getTableNumber() + " is already occupied.");
            }
        }

        for (OrderItem item : items) {
            if (item == null) {
                throw new InvalidOrderException("One of the order items is invalid.");
            }
            if (item.getQuantity() <= 0) {
                throw new InvalidOrderException("Quantity for item '" + item.getItemName() + "' must be positive.");
            }
            menuService.verifyItemAvailable(item.getItemId());
        }

        Map<Integer, Double> computedDeductions = inventoryDeductions == null
                ? menuRecipeService.buildInventoryDeductionsForOrder(items)
                : inventoryDeductions;

        if (!computedDeductions.isEmpty()) {
            inventoryService.deductStockForOrder(computedDeductions);
        }

        String orderNumber = "ORD-" + System.currentTimeMillis() % 10000000;
        Order order = new Order(orderNumber, customerId, tableId, employeeId);
        order.setSpecialNotes(specialNotes);
        order.setItems(items);
        order.setStatus(OrderStatus.CONFIRMED);

        boolean saved = orderRepository.createOrderWithItems(order);
        if (!saved) {
            throw new InvalidOrderException("Database operation failed while saving the order.");
        }

        if (tableId != null && tableId > 0) {
            tableRepository.updateStatus(tableId, TableStatus.OCCUPIED);
        }

        DataStructureManager.getInstance().getKitchenOrderQueue().enqueue(order, 5);
        com.restaurant.service.KitchenOrderProcessor.getInstance().submitOrder(order);

        ActivityLogger.getInstance().logOrderCreated(
                order.getOrderNumber(),
                order.getTableNumber() != null ? order.getTableNumber() : "Takeaway",
                order.getTotalAmount());

        LOGGER.info("Order created successfully: " + order.getOrderNumber() + " with total: " + order.getTotalAmount());
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId);
    }

    public boolean updateOrderStatus(int orderId, OrderStatus status) {
        return orderRepository.updateStatus(orderId, status);
    }

    public boolean cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId);
        if (order != null && order.getTableId() != null) {
            tableRepository.updateStatus(order.getTableId(), TableStatus.AVAILABLE);
        }
        return orderRepository.updateStatus(orderId, OrderStatus.CANCELLED);
    }
}
