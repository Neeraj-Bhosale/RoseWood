package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    @DisplayName("DatabaseConfig instance singleton integrity test")
    public void testDatabaseConfigSingleton() {
        DatabaseConfig config1 = DatabaseConfig.getInstance();
        DatabaseConfig config2 = DatabaseConfig.getInstance();
        assertNotNull(config1);
        assertSame(config1, config2, "DatabaseConfig should be a singleton instance");
        assertNotNull(config1.getUrl());
        assertNotNull(config1.getUsername());
    }
}
