package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public List<MenuCategory> findAllCategories() {
        List<MenuCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM menu_categories ORDER BY display_order ASC, name ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(new MenuCategory(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("display_order")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching menu categories", e);
        }
        return categories;
    }

    public List<MenuItem> findAllItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT m.*, c.name as category_name FROM menu_items m " +
                     "JOIN menu_categories c ON m.category_id = c.category_id " +
                     "ORDER BY c.display_order, m.name";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapResultSetToMenuItem(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching menu items", e);
        }
        return items;
    }

    public MenuItem findItemById(int itemId) {
        String sql = "SELECT m.*, c.name as category_name FROM menu_items m " +
                     "JOIN menu_categories c ON m.category_id = c.category_id " +
                     "WHERE m.item_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMenuItem(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding menu item by ID: " + itemId, e);
        }
        return null;
    }

    public boolean createItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (category_id, name, description, price, preparation_time, is_available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getCategoryId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getDescription());
            stmt.setDouble(4, item.getPrice());
            stmt.setInt(5, item.getPreparationTime());
            stmt.setBoolean(6, item.isAvailable());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        item.setItemId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating menu item: " + item.getName(), e);
        }
        return false;
    }

    public boolean updateItem(MenuItem item) {
        String sql = "UPDATE menu_items SET category_id = ?, name = ?, description = ?, price = ?, preparation_time = ?, is_available = ? WHERE item_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getCategoryId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getDescription());
            stmt.setDouble(4, item.getPrice());
            stmt.setInt(5, item.getPreparationTime());
            stmt.setBoolean(6, item.isAvailable());
            stmt.setInt(7, item.getItemId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating menu item: " + item.getItemId(), e);
        }
    }

    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting menu item: " + itemId, e);
        }
    }

    public boolean createCategory(MenuCategory cat) {
        String sql = "INSERT INTO menu_categories (name, description, display_order) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cat.getName());
            stmt.setString(2, cat.getDescription());
            stmt.setInt(3, cat.getDisplayOrder());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) cat.setCategoryId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating menu category: " + cat.getName(), e);
        }
        return false;
    }

    public MenuCategory findCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String sql = "SELECT * FROM menu_categories WHERE LOWER(name) = ? LIMIT 1";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MenuCategory(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("display_order")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding category: " + name, e);
        }
        return null;
    }

    public boolean deleteAllItems() {
        String sql = "DELETE FROM menu_items";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error clearing all menu items", e);
        }
    }

    public boolean deleteAllCategories() {
        String sql = "DELETE FROM menu_categories";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error clearing all menu categories", e);
        }
    }

    private MenuItem mapResultSetToMenuItem(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem(
                rs.getInt("item_id"),
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("preparation_time"),
                rs.getBoolean("is_available")
        );
        item.setCategoryName(rs.getString("category_name"));
        item.setImagePath(rs.getString("image_path"));
        return item;
    }
}
