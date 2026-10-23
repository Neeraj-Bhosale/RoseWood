package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public Customer findById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding customer by ID: " + customerId, e);
        }
        return null;
    }

    public Customer findByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding customer by phone: " + phone, e);
        }
        return null;
    }

    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY total_spent DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching all customers", e);
        }
        return customers;
    }

    public boolean create(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, email, address, loyalty_points, total_visits, total_spent) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getLoyaltyPoints());
            stmt.setInt(6, customer.getTotalVisits());
            stmt.setDouble(7, customer.getTotalSpent());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        customer.setCustomerId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating customer: " + customer.getName(), e);
        }
        return false;
    }

    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ?, loyalty_points = ?, total_visits = ?, total_spent = ? WHERE customer_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getLoyaltyPoints());
            stmt.setInt(6, customer.getTotalVisits());
            stmt.setDouble(7, customer.getTotalSpent());
            stmt.setInt(8, customer.getCustomerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating customer: " + customer.getCustomerId(), e);
        }
    }

    public boolean delete(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting customer: " + customerId, e);
        }
    }

    public boolean deleteAll() {
        String sql = "DELETE FROM customers";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error clearing all customers", e);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setLoyaltyPoints(rs.getInt("loyalty_points"));
        c.setTotalVisits(rs.getInt("total_visits"));
        c.setTotalSpent(rs.getDouble("total_spent"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) c.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) c.setUpdatedAt(updated.toLocalDateTime());
        return c;
    }
}
