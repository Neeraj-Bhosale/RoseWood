package com.restaurant.config;

import com.restaurant.exception.DatabaseOperationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe Database Configuration and Connection Factory.
 */
public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static volatile DatabaseConfig instance;
    private final String url;
    private final String username;
    private final String password;
    private final String driver;

    private DatabaseConfig() {
        AppConfig appConfig = AppConfig.getInstance();
        this.url = appConfig.getProperty("db.url");
        this.username = appConfig.getProperty("db.username");
        this.password = appConfig.getProperty("db.password");
        this.driver = appConfig.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        try {
            Class.forName(driver);
            LOGGER.info("MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found in classpath!", e);
            throw new DatabaseOperationException("JDBC Driver class not found: " + driver, e);
        }
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }

    /**
     * Establishes and returns a fresh JDBC Connection.
     * @return active java.sql.Connection
     * @throws DatabaseOperationException if connection cannot be opened
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database at: " + url, e);
            throw new DatabaseOperationException("Database connection failed. Please ensure MySQL is running.", e);
        }
    }

    /**
     * Diagnostic method to test connectivity.
     * @return true if connection succeeded and valid, false otherwise.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            LOGGER.warning("Database connectivity test failed: " + e.getMessage());
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
}
