package com.restaurant.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes and verifies essential database tables on application launch.
 * Ensures tables such as `menu_item_ingredients` and `inventory_logs` exist.
 */
public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeTables() {
        String createRecipesTable = "CREATE TABLE IF NOT EXISTS menu_item_ingredients (" +
                "    recipe_id INT AUTO_INCREMENT PRIMARY KEY," +
                "    menu_item_id INT NOT NULL," +
                "    inventory_id INT NOT NULL," +
                "    quantity_required DECIMAL(10, 2) NOT NULL CHECK (quantity_required > 0)," +
                "    unit VARCHAR(20) NOT NULL DEFAULT 'kg'," +
                "    FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id) ON DELETE CASCADE," +
                "    FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id) ON DELETE CASCADE," +
                "    UNIQUE (menu_item_id, inventory_id)," +
                "    INDEX idx_menu_recipe_item (menu_item_id)" +
                ") ENGINE=InnoDB;";

        String createInventoryLogsTable = "CREATE TABLE IF NOT EXISTS inventory_logs (" +
                "    log_id INT AUTO_INCREMENT PRIMARY KEY," +
                "    order_number VARCHAR(50) NOT NULL," +
                "    inventory_id INT NOT NULL," +
                "    ingredient_name VARCHAR(100) NOT NULL," +
                "    deducted_quantity DECIMAL(10, 2) NOT NULL," +
                "    unit VARCHAR(20) NOT NULL," +
                "    previous_stock DECIMAL(10, 2) NOT NULL," +
                "    remaining_stock DECIMAL(10, 2) NOT NULL," +
                "    notes VARCHAR(255)," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    INDEX idx_inv_log_order (order_number)," +
                "    INDEX idx_inv_log_date (created_at)" +
                ") ENGINE=InnoDB;";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createRecipesTable);
            stmt.execute(createInventoryLogsTable);
            LOGGER.info("Database schema tables verified (menu_item_ingredients & inventory_logs).");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database table auto-initialization warning: " + e.getMessage());
        }
    }
}
