package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public List<Supplier> findAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching suppliers", e);
        }
        return list;
    }

    public Supplier findById(int supplierId) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding supplier by ID: " + supplierId, e);
        }
        return null;
    }

    public boolean create(Supplier s) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address, supplied_category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getContactPerson());
            stmt.setString(3, s.getPhone());
            stmt.setString(4, s.getEmail());
            stmt.setString(5, s.getAddress());
            stmt.setString(6, s.getSuppliedCategory());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        s.setSupplierId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating supplier: " + s.getName(), e);
        }
        return false;
    }

    public boolean update(Supplier s) {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ?, supplied_category = ? WHERE supplier_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getContactPerson());
            stmt.setString(3, s.getPhone());
            stmt.setString(4, s.getEmail());
            stmt.setString(5, s.getAddress());
            stmt.setString(6, s.getSuppliedCategory());
            stmt.setInt(7, s.getSupplierId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating supplier: " + s.getSupplierId(), e);
        }
    }

    public boolean delete(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting supplier: " + supplierId, e);
        }
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setSupplierId(rs.getInt("supplier_id"));
        s.setName(rs.getString("name"));
        s.setContactPerson(rs.getString("contact_person"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setSuppliedCategory(rs.getString("supplied_category"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }
}
