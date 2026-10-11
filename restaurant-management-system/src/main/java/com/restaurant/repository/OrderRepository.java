package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.enums.OrderStatus;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public Order findById(int orderId) {
        String sql = "SELECT o.*, c.name as customer_name, t.table_number, u.full_name as employee_name " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "LEFT JOIN restaurant_tables t ON o.table_id = t.table_id " +
                     "JOIN users u ON o.employee_id = u.user_id " +
                     "WHERE o.order_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsByOrderId(order.getOrderId()));
                    return order;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding order by ID: " + orderId, e);
        }
        return null;
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.name as customer_name, t.table_number, u.full_name as employee_name " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "LEFT JOIN restaurant_tables t ON o.table_id = t.table_id " +
                     "JOIN users u ON o.employee_id = u.user_id " +
                     "ORDER BY o.order_date_time DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching orders", e);
        }
        return orders;
    }

    public List<Order> findActiveKitchenOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.name as customer_name, t.table_number, u.full_name as employee_name " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "LEFT JOIN restaurant_tables t ON o.table_id = t.table_id " +
                     "JOIN users u ON o.employee_id = u.user_id " +
                     "WHERE o.status IN ('CONFIRMED', 'PREPARING', 'READY') " +
                     "ORDER BY o.order_date_time ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findItemsByOrderId(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching kitchen orders", e);
        }
        return orders;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, m.name as item_name FROM order_items oi " +
                     "JOIN menu_items m ON oi.item_id = m.item_id WHERE oi.order_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setItemId(rs.getInt("item_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setSpecialInstructions(rs.getString("special_instructions"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    item.setStatus(rs.getString("status"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching order items for order: " + orderId, e);
        }
        return items;
    }

    public boolean createOrderWithItems(Order order) {
        String insertOrderSql = "INSERT INTO orders (order_number, customer_id, table_id, employee_id, order_date_time, status, subtotal, discount_amount, tax_amount, service_charge, total_amount, special_notes) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, special_instructions, subtotal, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false); // Transaction begins

            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setString(1, order.getOrderNumber());
                if (order.getCustomerId() != null) orderStmt.setInt(2, order.getCustomerId());
                else orderStmt.setNull(2, Types.INTEGER);

                if (order.getTableId() != null) orderStmt.setInt(3, order.getTableId());
                else orderStmt.setNull(3, Types.INTEGER);

                orderStmt.setInt(4, order.getEmployeeId());
                orderStmt.setTimestamp(5, Timestamp.valueOf(order.getOrderDateTime()));
                orderStmt.setString(6, order.getStatus().name());
                orderStmt.setDouble(7, order.getSubtotal());
                orderStmt.setDouble(8, order.getDiscountAmount());
                orderStmt.setDouble(9, order.getTaxAmount());
                orderStmt.setDouble(10, order.getServiceCharge());
                orderStmt.setDouble(11, order.getTotalAmount());
                orderStmt.setString(12, order.getSpecialNotes());

                int affected = orderStmt.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setOrderId(keys.getInt(1));
                    }
                }
            }

            // Insert line items
            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                for (OrderItem item : order.getItems()) {
                    itemStmt.setInt(1, order.getOrderId());
                    itemStmt.setInt(2, item.getItemId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getUnitPrice());
                    itemStmt.setString(5, item.getSpecialInstructions());
                    itemStmt.setDouble(6, item.getSubtotal());
                    itemStmt.setString(7, item.getStatus() != null ? item.getStatus() : "PENDING");
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit(); // Transaction commits
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignored */ }
            }
            throw new DatabaseOperationException("Error saving order transaction: " + order.getOrderNumber(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* ignored */ }
            }
        }
    }

    public boolean updateStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating status for order: " + orderId, e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setOrderNumber(rs.getString("order_number"));
        int custId = rs.getInt("customer_id");
        if (!rs.wasNull()) o.setCustomerId(custId);
        o.setCustomerName(rs.getString("customer_name"));

        int tableId = rs.getInt("table_id");
        if (!rs.wasNull()) o.setTableId(tableId);
        o.setTableNumber(rs.getString("table_number"));

        o.setEmployeeId(rs.getInt("employee_id"));
        o.setEmployeeName(rs.getString("employee_name"));
        Timestamp ts = rs.getTimestamp("order_date_time");
        if (ts != null) o.setOrderDateTime(ts.toLocalDateTime());
        o.setStatus(OrderStatus.valueOf(rs.getString("status")));
        o.setSubtotal(rs.getDouble("subtotal"));
        o.setDiscountAmount(rs.getDouble("discount_amount"));
        o.setTaxAmount(rs.getDouble("tax_amount"));
        o.setServiceCharge(rs.getDouble("service_charge"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setSpecialNotes(rs.getString("special_notes"));
        return o;
    }
}
