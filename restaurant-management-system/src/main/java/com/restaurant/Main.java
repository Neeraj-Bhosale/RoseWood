package com.restaurant;

// Import the database configuration so the app can connect to MySQL.
import java.util.logging.Level;
import java.util.logging.Logger;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.service.AsyncReportGenerator;
import com.restaurant.service.DatabaseHealthMonitor;
import com.restaurant.service.KitchenOrderProcessor;
import com.restaurant.util.ActivityLogger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Launcher for Restaurant Management System.
 *
 * This class is the starting point of the whole project.
 * Responsible for:
 * 1. Starting the JavaFX app
 * 2. Loading screens (Login: 900x600, Dashboard: 1280x800 default)
 * 3. Starting background services
 * 4. Handling errors
 * 5. Clean shutdown
 */
public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static Stage primaryStage;

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.log(Level.SEVERE,
                    "Uncaught exception on thread [" + thread.getName() + "]", throwable);
            ActivityLogger.getInstance().logError(
                    "UncaughtException[" + thread.getName() + "]",
                    throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getName());
        });

        LOGGER.info("Global uncaught exception handler installed.");

        KitchenOrderProcessor.getInstance().start();
        DatabaseHealthMonitor.getInstance().startMonitoring();

        ActivityLogger.getInstance().logInfo("Application started — RoseWood RMS v1.0");
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("RoseWood — Restaurant Management System");

        // Start a new thread to test the database connection before the app fully loads.
        new Thread(() -> {
            try {
                boolean connected = DatabaseConfig.getInstance().testConnection();
                if (connected) {
                    com.restaurant.config.DatabaseInitializer.initializeTables();
                    LOGGER.info("Successfully connected to MySQL database on startup.");
                    ActivityLogger.getInstance().logInfo("MySQL connection verified on startup.");
                } else {
                    LOGGER.warning("Could not connect to MySQL. Verify credentials in application.properties.");
                    ActivityLogger.getInstance().logError("Startup", "MySQL connection test failed.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception during startup DB check", e);
                ActivityLogger.getInstance().logError("Startup DB check", e.getMessage());
            }
        }, "Startup-DB-Check-Thread").start();

        loadLoginScreen();
    }

    @Override
    public void stop() {
        LOGGER.info("Application shutting down — stopping background services...");

        KitchenOrderProcessor.getInstance().stop();
        DatabaseHealthMonitor.getInstance().stopMonitoring();
        AsyncReportGenerator.getInstance().shutdown();

        ActivityLogger.getInstance().logInfo("Application stopped — all services shut down.");
        LOGGER.info("All background services stopped. Goodbye.");
    }

    // -----------------------------------------------------------------------
    // Screen navigation helpers (Default Screen Ratios)
    // -----------------------------------------------------------------------

    /**
     * Load and display the login screen at standard 900x600 resolution.
     */
    public static void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    Main.class.getResource("/css/application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load login screen", e);
            ActivityLogger.getInstance().logError("loadLoginScreen", e.getMessage());
        }
    }

    /**
     * Load and display the staff registration screen at 900x650 resolution.
     */
    public static void loadRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 650);
            scene.getStylesheets().add(
                    Main.class.getResource("/css/application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load register screen", e);
            ActivityLogger.getInstance().logError("loadRegisterScreen", e.getMessage());
        }
    }

    /**
     * Load and display the main restaurant dashboard at default 1280x800 resolution.
     */
    public static void loadMainDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 800);
            scene.getStylesheets().add(
                    Main.class.getResource("/css/application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load dashboard screen", e);
            ActivityLogger.getInstance().logError("loadMainDashboard", e.getMessage());
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
