package com.restaurant.data;

import com.restaurant.model.Customer;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;

import java.util.*;

/**
 * Demonstrates centralized application state management utilizing diverse Java Collections:
 * - HashMap for fast O(1) key-based lookups.
 * - HashSet for maintaining distinct unique active sessions/customers.
 * - ArrayList for flexible indexed item sequences.
 * - PriorityQueue for kitchen preparation dispatch.
 */
public class DataStructureManager {
    private static DataStructureManager instance;

    // Fast O(1) in-memory item lookup cache
    private final Map<Integer, MenuItem> menuCache = new HashMap<>();

    // Fast unique phone lookup for customers
    private final Map<String, Customer> customerPhoneMap = new HashMap<>();

    // Set of active logged-in usernames
    private final Set<String> activeUserSessions = new HashSet<>();

    // Kitchen dispatch queue
    private final OrderQueue kitchenOrderQueue = new OrderQueue();

    private DataStructureManager() {}

    public static synchronized DataStructureManager getInstance() {
        if (instance == null) {
            instance = new DataStructureManager();
        }
        return instance;
    }

    public void cacheMenuItems(List<MenuItem> items) {
        menuCache.clear();
        if (items != null) {
            for (MenuItem item : items) {
                menuCache.put(item.getItemId(), item);
            }
        }
    }

    public MenuItem getMenuItemById(int itemId) {
        return menuCache.get(itemId);
    }

    public void registerCustomerInIndex(Customer customer) {
        if (customer != null && customer.getPhone() != null) {
            customerPhoneMap.put(customer.getPhone(), customer);
        }
    }

    public Customer findCustomerByPhone(String phone) {
        return customerPhoneMap.get(phone);
    }

    public void registerSession(String username) {
        if (username != null) {
            activeUserSessions.add(username);
        }
    }

    public void endSession(String username) {
        if (username != null) {
            activeUserSessions.remove(username);
        }
    }

    public boolean isUserActive(String username) {
        return activeUserSessions.contains(username);
    }

    public OrderQueue getKitchenOrderQueue() {
        return kitchenOrderQueue;
    }
}
