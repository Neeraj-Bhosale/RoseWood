package com.restaurant;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.service.AsyncReportGenerator;
import com.restaurant.service.DatabaseHealthMonitor;
import com.restaurant.service.KitchenOrderProcessor;
import com.restaurant.util.ActivityLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Application Launcher for Restaurant Management System.
 *
 * Integrates three advanced Java concepts:
 *  1. EXCEPTION HANDLING  — global uncaught exception handler catches any
 *                            unhandled RuntimeException on any thread and logs it.
 *  2. MULTI-THREADING     — starts KitchenOrderProcessor (background daemon thread)
 *                           and DatabaseHealthMonitor (ScheduledExecutorService) on launch;
 *                           stops them cleanly in stop().
 *  3. FILE HANDLING       — ActivityLogger records every major lifecycle event to
 *                           logs/activity.log using BufferedWriter.
 */
public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static Stage primaryStage;

    // -----------------------------------------------------------------------
    // JavaFX Application lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void init() {
        // EXCEPTION HANDLING — install a global handler that catches any
        // uncaught exception on ANY thread (UI, worker, scheduled, etc.)
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.log(Level.SEVERE,
                    "Uncaught exception on thread [" + thread.getName() + "]", throwable);
            ActivityLogger.getInstance().logError(
                    "UncaughtException[" + thread.getName() + "]",
                    throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getName());
        });

        LOGGER.info("Global uncaught exception handler installed.");

        // MULTI-THREADING — start background services before the UI appears
        KitchenOrderProcessor.getInstance().start();      // daemon worker thread
        DatabaseHealthMonitor.getInstance().startMonitoring(); // scheduled pinger

        // FILE HANDLING — log application startup
        ActivityLogger.getInstance().logInfo("Application started — RoseWood RMS v1.0");
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("RoseWood — Restaurant Management System");

        // Verify DB connectivity in background (MULTI-THREADING: anonymous Thread)
        new Thread(() -> {
            try {
                boolean connected = DatabaseConfig.getInstance().testConnection();
                if (connected) {
                    LOGGER.info("Successfully connected to MySQL database on startup.");
                    ActivityLogger.getInstance().logInfo("MySQL connection verified on startup.");
                } else {
                    LOGGER.warning("Could not connect to MySQL. Verify credentials in application.properties.");
                    ActivityLogger.getInstance().logError("Startup", "MySQL connection test failed.");
                }
            } catch (Exception e) {
                // EXCEPTION HANDLING — catch DB connection failure gracefully
                LOGGER.log(Level.SEVERE, "Exception during startup DB check", e);
                ActivityLogger.getInstance().logError("Startup DB check", e.getMessage());
            }
        }, "Startup-DB-Check-Thread").start();

        loadLoginScreen();
    }

    /**
     * Called by JavaFX when the application is closing.
     * MULTI-THREADING: gracefully shuts down all background threads/executors.
     * FILE HANDLING: logs the shutdown event.
     */
    @Override
    public void stop() {
        LOGGER.info("Application shutting down — stopping background services...");

        // Shut down multi-threaded services cleanly
        KitchenOrderProcessor.getInstance().stop();
        DatabaseHealthMonitor.getInstance().stopMonitoring();
        AsyncReportGenerator.getInstance().shutdown();

        // FILE HANDLING — write shutdown event to log
        ActivityLogger.getInstance().logInfo("Application stopped — all services shut down.");

        LOGGER.info("All background services stopped. Goodbye.");
    }

    // -----------------------------------------------------------------------
    // Screen navigation helpers
    // -----------------------------------------------------------------------

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
            // EXCEPTION HANDLING — log and prevent silent failure
            LOGGER.log(Level.SEVERE, "Failed to load login screen", e);
            ActivityLogger.getInstance().logError("loadLoginScreen", e.getMessage());
        }
    }

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
            // EXCEPTION HANDLING — log and prevent silent failure
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

