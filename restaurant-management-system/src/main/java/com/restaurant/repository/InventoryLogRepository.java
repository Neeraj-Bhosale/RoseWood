package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.InventoryDeductionDetail;
import com.restaurant.model.InventoryLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public boolean saveBatch(List<InventoryDeductionDetail> details) {
        if (details == null || details.isEmpty()) return true;

        String sql = "INSERT INTO inventory_logs (order_number, inventory_id, ingredient_name, " +
                "deducted_quantity, unit, previous_stock, remaining_stock, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (InventoryDeductionDetail d : details) {
                stmt.setString(1, d.getOrderNumber());
                stmt.setInt(2, d.getInventoryId());
                stmt.setString(3, d.getIngredientName());
                stmt.setDouble(4, d.getDeductedQuantity());
                stmt.setString(5, d.getUnit());
                stmt.setDouble(6, d.getPreviousStock());
                stmt.setDouble(7, d.getRemainingStock());
                String note = d.getMenuItemName() != null ? "Used for dish: " + d.getMenuItemName() + " (Qty: " + d.getOrderQuantity() + ")" : "Order deduction";
                stmt.setString(8, note);
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error saving inventory deduction audit logs", e);
        }
    }

    public List<InventoryLog> findAll(int limit) {
        List<InventoryLog> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory_logs ORDER BY log_id DESC LIMIT ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit > 0 ? limit : 100);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching inventory audit logs", e);
        }
        return list;
    }

    public List<InventoryLog> findByOrderNumber(String orderNumber) {
        List<InventoryLog> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory_logs WHERE order_number = ? ORDER BY log_id ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching inventory logs for order: " + orderNumber, e);
        }
        return list;
    }

    public boolean deleteAll() {
        String sql = "DELETE FROM inventory_logs";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error clearing inventory logs", e);
        }
    }

    private InventoryLog mapResultSetToLog(ResultSet rs) throws SQLException {
        InventoryLog log = new InventoryLog();
        log.setLogId(rs.getInt("log_id"));
        log.setOrderNumber(rs.getString("order_number"));
        log.setInventoryId(rs.getInt("inventory_id"));
        log.setIngredientName(rs.getString("ingredient_name"));
        log.setDeductedQuantity(rs.getDouble("deducted_quantity"));
        log.setUnit(rs.getString("unit"));
        log.setPreviousStock(rs.getDouble("previous_stock"));
        log.setRemainingStock(rs.getDouble("remaining_stock"));
        log.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) log.setCreatedAt(ts.toLocalDateTime());
        return log;
    }
}
