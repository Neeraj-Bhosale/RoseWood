package com.restaurant.config;

// Java classes used to read the property file and store configuration values.
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * This class stores all application settings in one place.
 * It loads values from application.properties and gives simple methods to read them.
 * In simple words: this class acts like a settings box for the whole project.
 */
public class AppConfig {
    // This holds the single object of this class.
    private static AppConfig instance;
    // This object stores key-value pairs like name=value.
    private final Properties properties = new Properties();

    // Private constructor prevents creating multiple copies.
    private AppConfig() {
        loadProperties();
    }

    // This method gives the single instance of AppConfig.
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    // This method reads the property file and puts values into the Properties object.
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/application.properties")) {

            // If the file is missing, use safe default values instead.
            if (input == null) {
                setDefaultValues();
                return;
            }

            // Read the file using UTF-8 encoding so special characters are handled well.
            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (Exception e) {
            // If there is any reading error, fall back to default values.
            setDefaultValues();
        }
    }

    // This method stores default values in case the file is missing or broken.
    private void setDefaultValues() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/restaurant_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "Pune411019");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("app.name", "RoseWood Restaurant Management System");
        properties.setProperty("app.currency", "\u20B9");
        properties.setProperty("app.taxRate", "5.0");
        properties.setProperty("app.serviceChargeRate", "2.5");
        properties.setProperty("restaurant.name", "RoseWood Fine Dining");
        properties.setProperty("restaurant.address", "104 Gourmet Boulevard, Silicon City");
        properties.setProperty("restaurant.phone", "+91 98765 43210");
        properties.setProperty("restaurant.email", "contact@rosewood.com");
    }

    // Returns a string value for the given key.
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Returns a string value or a default value if the key is not found.
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // A simpler method to read a string.
    public String getString(String key) {
        return getProperty(key);
    }

    // Converts a stored value into an integer.
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // Converts a stored value into a double.
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
