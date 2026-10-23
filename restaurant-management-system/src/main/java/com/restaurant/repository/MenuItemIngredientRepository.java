package com.restaurant.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.MenuItemIngredient;

public class MenuItemIngredientRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public List<MenuItemIngredient> findByMenuItemId(int menuItemId) {
        String sql = "SELECT mii.*, i.ingredient_name, i.unit " +
                "FROM menu_item_ingredients mii " +
                "JOIN inventory i ON mii.inventory_id = i.inventory_id " +
                "WHERE mii.menu_item_id = ? ORDER BY mii.inventory_id ASC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, menuItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<MenuItemIngredient> ingredients = new ArrayList<>();
                while (rs.next()) {
                    ingredients.add(mapResultSetToIngredient(rs));
                }
                return ingredients;
            }
        } catch (SQLException e) {
            if (isMissingTable(e)) {
                return new ArrayList<>();
            }
            throw new DatabaseOperationException("Error fetching ingredients for menu item: " + menuItemId, e);
        }
    }

    public List<MenuItemIngredient> findAll() {
        String sql = "SELECT mii.*, i.ingredient_name, i.unit " +
                "FROM menu_item_ingredients mii " +
                "JOIN inventory i ON mii.inventory_id = i.inventory_id " +
                "ORDER BY mii.menu_item_id, mii.inventory_id ASC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<MenuItemIngredient> ingredients = new ArrayList<>();
            while (rs.next()) {
                ingredients.add(mapResultSetToIngredient(rs));
            }
            return ingredients;
        } catch (SQLException e) {
            if (isMissingTable(e)) {
                return new ArrayList<>();
            }
            throw new DatabaseOperationException("Error fetching all menu ingredients", e);
        }
    }

    public boolean create(MenuItemIngredient ingredient) {
        String sql = "INSERT INTO menu_item_ingredients (menu_item_id, inventory_id, quantity_required, unit) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity_required = VALUES(quantity_required), unit = VALUES(unit)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ingredient.getMenuItemId());
            stmt.setInt(2, ingredient.getInventoryId());
            stmt.setDouble(3, ingredient.getQuantityRequired());
            stmt.setString(4, ingredient.getUnit() != null ? ingredient.getUnit() : "kg");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        ingredient.setRecipeId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            if (isMissingTable(e)) {
                return false;
            }
            throw new DatabaseOperationException("Error creating ingredient mapping for menu item: " + ingredient.getMenuItemId(), e);
        }
        return false;
    }

    public boolean delete(int recipeId) {
        String sql = "DELETE FROM menu_item_ingredients WHERE recipe_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting recipe ingredient: " + recipeId, e);
        }
    }

    public boolean deleteByMenuItemId(int menuItemId) {
        String sql = "DELETE FROM menu_item_ingredients WHERE menu_item_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, menuItemId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting recipe ingredients for menu item: " + menuItemId, e);
        }
    }

    public boolean deleteAll() {
        String sql = "DELETE FROM menu_item_ingredients";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error clearing all recipe ingredients", e);
        }
    }

    private boolean isMissingTable(SQLException e) {
        return e.getSQLState() != null && e.getSQLState().equals("42S02");
    }

    private MenuItemIngredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        MenuItemIngredient ingredient = new MenuItemIngredient();
        ingredient.setRecipeId(rs.getInt("recipe_id"));
        ingredient.setMenuItemId(rs.getInt("menu_item_id"));
        ingredient.setInventoryId(rs.getInt("inventory_id"));
        ingredient.setIngredientName(rs.getString("ingredient_name"));
        ingredient.setQuantityRequired(rs.getDouble("quantity_required"));
        ingredient.setUnit(rs.getString("unit"));
        return ingredient;
    }
}
