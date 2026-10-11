package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.enums.TableStatus;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.RestaurantTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public RestaurantTable findById(int tableId) {
        String sql = "SELECT * FROM restaurant_tables WHERE table_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tableId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTable(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding table by ID: " + tableId, e);
        }
        return null;
    }

    public List<RestaurantTable> findAll() {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM restaurant_tables ORDER BY table_number ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tables.add(mapResultSetToTable(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching tables", e);
        }
        return tables;
    }

    public boolean create(RestaurantTable table) {
        String sql = "INSERT INTO restaurant_tables (table_number, capacity, location, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getLocation());
            stmt.setString(4, table.getStatus().name());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        table.setTableId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating table: " + table.getTableNumber(), e);
        }
        return false;
    }

    public boolean update(RestaurantTable table) {
        String sql = "UPDATE restaurant_tables SET table_number = ?, capacity = ?, location = ?, status = ? WHERE table_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getLocation());
            stmt.setString(4, table.getStatus().name());
            stmt.setInt(5, table.getTableId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating table: " + table.getTableId(), e);
        }
    }

    public boolean updateStatus(int tableId, TableStatus status) {
        String sql = "UPDATE restaurant_tables SET status = ? WHERE table_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, tableId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating status for table: " + tableId, e);
        }
    }

    public boolean delete(int tableId) {
        String sql = "DELETE FROM restaurant_tables WHERE table_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tableId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting table: " + tableId, e);
        }
    }

    private RestaurantTable mapResultSetToTable(ResultSet rs) throws SQLException {
        return new RestaurantTable(
                rs.getInt("table_id"),
                rs.getString("table_number"),
                rs.getInt("capacity"),
                rs.getString("location"),
                TableStatus.valueOf(rs.getString("status"))
        );
    }
}
