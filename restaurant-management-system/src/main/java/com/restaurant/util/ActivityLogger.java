package com.restaurant.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FILE HANDLING — Activity Logger
 *
 * Demonstrates Java file I/O using FileWriter and BufferedWriter.
 * Appends timestamped log entries to logs/activity.log for every
 * significant application event (login, logout, order, payment, error).
 *
 * Thread-safe: uses synchronized append to prevent interleaved writes
 * when multiple threads call log() concurrently.
 */
public class ActivityLogger {

    private static final Logger LOGGER = Logger.getLogger(ActivityLogger.class.getName());
    private static final String LOG_DIR  = "logs";
    private static final String LOG_FILE = "logs/activity.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Singleton instance
    private static ActivityLogger instance;

    // Private constructor — ensures log directory exists on first use
    private ActivityLogger() {
        ensureLogDirectoryExists();
    }

    /** Returns the single shared instance (lazy-initialised, thread-safe). */
    public static synchronized ActivityLogger getInstance() {
        if (instance == null) {
            instance = new ActivityLogger();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Public logging API
    // -----------------------------------------------------------------------

    /** Log a successful user login. */
    public void logLogin(String username, String role) {
        writeEntry("LOGIN", "User '" + username + "' logged in as " + role);
    }

    /** Log a user logout event. */
    public void logLogout(String username) {
        writeEntry("LOGOUT", "User '" + username + "' logged out");
    }

    /** Log a new order being placed. */
    public void logOrderCreated(String orderNumber, String tableNumber, double total) {
        writeEntry("ORDER_CREATED",
                String.format("Order %s | Table: %s | Total: %.2f", orderNumber, tableNumber, total));
    }

    /** Log a payment being completed. */
    public void logPayment(String orderNumber, double amount, String method) {
        writeEntry("PAYMENT",
                String.format("Order %s | Amount: %.2f | Method: %s", orderNumber, amount, method));
    }

    /** Log a bill/receipt being saved to disk. */
    public void logReceiptSaved(String orderNumber, String filePath) {
        writeEntry("RECEIPT_SAVED",
                String.format("Order %s | File: %s", orderNumber, filePath));
    }

    /** Log a report being exported. */
    public void logReportExported(String reportType, String filePath) {
        writeEntry("REPORT_EXPORTED",
                String.format("Type: %s | File: %s", reportType, filePath));
    }

    /** Log live inventory deductions for an order with exact details. */
    public void logInventoryDeductions(String orderNumber, java.util.List<com.restaurant.model.InventoryDeductionDetail> details) {
        if (details == null || details.isEmpty()) {
            writeEntry("INVENTORY_DEDUCT", "Order " + orderNumber + " | No raw stock deductions required");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(orderNumber).append(" | Live Deductions: ");
        for (int i = 0; i < details.size(); i++) {
            com.restaurant.model.InventoryDeductionDetail d = details.get(i);
            sb.append(String.format("%s: -%.2f %s (Rem: %.2f %s)",
                    d.getIngredientName(), d.getDeductedQuantity(), d.getUnit(), d.getRemainingStock(), d.getUnit()));
            if (i < details.size() - 1) sb.append("; ");
        }
        writeEntry("INVENTORY_DEDUCT", sb.toString());
    }

    /** Log any application-level error with the exception message. */
    public void logError(String context, String errorMessage) {
        writeEntry("ERROR", context + " | " + errorMessage);
    }

    /** Log a generic informational event. */
    public void logInfo(String message) {
        writeEntry("INFO", message);
    }

    // -----------------------------------------------------------------------
    // Core file-writing logic (FILE HANDLING)
    // -----------------------------------------------------------------------

    /**
     * Appends a formatted log entry to the activity log file.
     * Uses try-with-resources to guarantee the BufferedWriter is closed
     * even if an IOException occurs — demonstrating proper file handling.
     *
     * Marked synchronized so concurrent threads don't interleave lines.
     */
    private synchronized void writeEntry(String eventType, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logLine   = String.format("[%s] [%-16s] %s%n", timestamp, eventType, message);

        // try-with-resources: FileWriter(path, true) → append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logLine);
            writer.flush(); // ensure the OS buffer is flushed immediately
        } catch (IOException e) {
            // Use java.util.logging as fallback — never silently swallow errors
            LOGGER.log(Level.WARNING, "ActivityLogger: failed to write to " + LOG_FILE, e);
        }
    }

    /** Creates the logs/ directory if it does not already exist. */
    private void ensureLogDirectoryExists() {
        try {
            Path dir = Paths.get(LOG_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                LOGGER.info("ActivityLogger: created log directory → " + dir.toAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "ActivityLogger: could not create log directory", e);
        }
    }
}
