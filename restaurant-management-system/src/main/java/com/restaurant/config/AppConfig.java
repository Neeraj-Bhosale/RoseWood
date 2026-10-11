package com.restaurant.config;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton configuration loader that parses application.properties.
 */
public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static AppConfig instance;
    private final Properties properties = new Properties();

    private AppConfig() {
        loadProperties();
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config/application.properties")) {
            if (input != null) {
                try (java.io.Reader reader = new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            } else {
                LOGGER.warning("config/application.properties not found on classpath, using fallback defaults");
                setDefaults();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration properties", e);
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/restaurant_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "Pune411019");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("app.name", "RoseWood RMS");
        properties.setProperty("app.currency", "\u20B9");
        properties.setProperty("app.taxRate", "5.0");
        properties.setProperty("app.serviceChargeRate", "2.5");
        properties.setProperty("restaurant.name", "RoseWood Fine Dining");
        properties.setProperty("restaurant.address", "104 Gourmet Boulevard, Silicon City");
        properties.setProperty("restaurant.phone", "+91 98765 43210");
        properties.setProperty("restaurant.email", "contact@rosewood.com");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
