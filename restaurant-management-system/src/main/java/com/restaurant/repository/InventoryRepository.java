package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.InventoryItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public List<InventoryItem> findAll() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT i.*, s.name as supplier_name FROM inventory i " +
                     "LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
                     "ORDER BY i.quantity ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToInventoryItem(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching inventory items", e);
        }
        return list;
    }

    public List<InventoryItem> findLowStockItems() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT i.*, s.name as supplier_name FROM inventory i " +
                     "LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
                     "WHERE i.quantity <= i.minimum_stock ORDER BY i.quantity ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToInventoryItem(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching low-stock inventory items", e);
        }
        return list;
    }

    public boolean create(InventoryItem item) {
        String sql = "INSERT INTO inventory (ingredient_name, category, quantity, unit, minimum_stock, unit_cost, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getIngredientName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getQuantity());
            stmt.setString(4, item.getUnit());
            stmt.setDouble(5, item.getMinimumStock());
            stmt.setDouble(6, item.getUnitCost());
            if (item.getSupplierId() != null) stmt.setInt(7, item.getSupplierId());
            else stmt.setNull(7, Types.INTEGER);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        item.setInventoryId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating inventory item: " + item.getIngredientName(), e);
        }
        return false;
    }

    public boolean updateStock(int inventoryId, double newQuantity) {
        String sql = "UPDATE inventory SET quantity = ? WHERE inventory_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newQuantity);
            stmt.setInt(2, inventoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating inventory stock for ID: " + inventoryId, e);
        }
    }

    public boolean update(InventoryItem item) {
        String sql = "UPDATE inventory SET ingredient_name = ?, category = ?, quantity = ?, unit = ?, minimum_stock = ?, unit_cost = ?, supplier_id = ? " +
                     "WHERE inventory_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getIngredientName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getQuantity());
            stmt.setString(4, item.getUnit());
            stmt.setDouble(5, item.getMinimumStock());
            stmt.setDouble(6, item.getUnitCost());
            if (item.getSupplierId() != null) stmt.setInt(7, item.getSupplierId());
            else stmt.setNull(7, Types.INTEGER);
            stmt.setInt(8, item.getInventoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating inventory item: " + item.getInventoryId(), e);
        }
    }

    public boolean delete(int inventoryId) {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting inventory item: " + inventoryId, e);
        }
    }

    private InventoryItem mapResultSetToInventoryItem(ResultSet rs) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setInventoryId(rs.getInt("inventory_id"));
        item.setIngredientName(rs.getString("ingredient_name"));
        item.setCategory(rs.getString("category"));
        item.setQuantity(rs.getDouble("quantity"));
        item.setUnit(rs.getString("unit"));
        item.setMinimumStock(rs.getDouble("minimum_stock"));
        item.setUnitCost(rs.getDouble("unit_cost"));
        int supId = rs.getInt("supplier_id");
        if (!rs.wasNull()) item.setSupplierId(supId);
        item.setSupplierName(rs.getString("supplier_name"));
        Timestamp ts = rs.getTimestamp("last_updated");
        if (ts != null) item.setLastUpdated(ts.toLocalDateTime());
        return item;
    }
}
