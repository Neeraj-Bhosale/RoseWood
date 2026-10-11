package com.restaurant.service;

import com.restaurant.data.DataStructureManager;
import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Order;
import com.restaurant.repository.OrderRepository;

import java.util.List;

public class KitchenService {
    private final OrderRepository orderRepository;

    public KitchenService() {
        this.orderRepository = new OrderRepository();
    }

    public KitchenService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getActiveKitchenOrders() {
        return orderRepository.findActiveKitchenOrders();
    }

    public boolean markAsPreparing(int orderId) {
        return orderRepository.updateStatus(orderId, OrderStatus.PREPARING);
    }

    public boolean markAsReady(int orderId) {
        return orderRepository.updateStatus(orderId, OrderStatus.READY);
    }

    public boolean markAsServed(int orderId) {
        return orderRepository.updateStatus(orderId, OrderStatus.SERVED);
    }
}
