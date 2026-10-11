package com.restaurant.service;

import com.restaurant.model.Order;
import com.restaurant.util.ActivityLogger;
import com.restaurant.util.ReportExporter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MULTI-THREADING — Async Report Generator
 *
 * Wraps time-consuming report queries + CSV exports inside CompletableFuture
 * so they run on a background thread pool and never block the JavaFX UI thread.
 *
 * Demonstrates:
 *  - CompletableFuture.supplyAsync() with a custom ExecutorService
 *  - thenAccept() for UI callback on the FX thread via Platform.runLater
 *  - exceptionally() for clean async error handling
 *  - ExecutorService (fixed thread pool) management
 *
 * Usage example (from a Controller):
 *
 *   AsyncReportGenerator.getInstance().exportOrdersAsync(
 *       orders,
 *       "daily_orders",
 *       path -> showAlert("Saved to: " + path),
 *       err  -> showAlert("Export failed: " + err.getMessage())
 *   );
 */
public class AsyncReportGenerator {

    private static final Logger LOGGER =
            Logger.getLogger(AsyncReportGenerator.class.getName());

    // Singleton
    private static AsyncReportGenerator instance;

    /** 2-thread pool: one for reports, one for CSV export running in parallel if needed. */
    private final ExecutorService executor =
            Executors.newFixedThreadPool(2, runnable -> {
                Thread t = new Thread(runnable, "Report-Generator-Thread");
                t.setDaemon(true);
                return t;
            });

    private final ReportExporter reportExporter = new ReportExporter();
    private final ReportService  reportService  = new ReportService();

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    private AsyncReportGenerator() {}

    public static synchronized AsyncReportGenerator getInstance() {
        if (instance == null) {
            instance = new AsyncReportGenerator();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Async export methods
    // -----------------------------------------------------------------------

    /**
     * Exports a list of orders to CSV asynchronously.
     *
     * @param orders      order data to export
     * @param filename    base filename (no extension)
     * @param onSuccess   callback with the saved file path — called when done
     * @param onError     callback with the exception — called on failure
     * @return CompletableFuture so callers can chain more actions if needed
     */
    public CompletableFuture<String> exportOrdersAsync(
            List<Order> orders,
            String filename,
            Consumer<String> onSuccess,
            Consumer<Throwable> onError) {

        return CompletableFuture
                .supplyAsync(() -> {
                    // Runs on Report-Generator-Thread (NOT the JavaFX thread)
                    try {
                        LOGGER.info("AsyncReportGenerator: starting order CSV export...");
                        String path = reportExporter.exportOrdersToCSV(orders, filename);
                        ActivityLogger.getInstance().logReportExported("ORDERS_CSV", path);
                        return path;
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to export orders CSV: " + e.getMessage(), e);
                    }
                }, executor)
                .thenApply(path -> {
                    if (onSuccess != null) {
                        // Delegate back to caller — caller should wrap in Platform.runLater if JavaFX
                        onSuccess.accept(path);
                    }
                    return path;
                })
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "AsyncReportGenerator: export failed", ex);
                    ActivityLogger.getInstance().logError("AsyncReportGenerator", ex.getMessage());
                    if (onError != null) {
                        onError.accept(ex);
                    }
                    return null;
                });
    }

    /**
     * Fetches the sales-by-category report AND exports it to CSV, all asynchronously.
     *
     * @param onSuccess callback with the saved file path
     * @param onError   callback with the exception on failure
     * @return CompletableFuture for optional chaining
     */
    public CompletableFuture<String> exportSalesByCategoryAsync(
            Consumer<String> onSuccess,
            Consumer<Throwable> onError) {

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        LOGGER.info("AsyncReportGenerator: fetching sales by category...");
                        // DB query — can be slow, runs on background thread
                        Map<String, Double> data = reportService.getSalesByCategory();

                        LOGGER.info("AsyncReportGenerator: exporting sales CSV...");
                        String path = reportExporter.exportSalesByCategoryCSV(data);
                        ActivityLogger.getInstance().logReportExported("SALES_BY_CATEGORY", path);
                        return path;
                    } catch (IOException e) {
                        throw new RuntimeException("CSV export failed: " + e.getMessage(), e);
                    }
                }, executor)
                .thenApply(path -> {
                    if (onSuccess != null) onSuccess.accept(path);
                    return path;
                })
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "AsyncReportGenerator: sales category export failed", ex);
                    ActivityLogger.getInstance().logError("AsyncReportGenerator", ex.getMessage());
                    if (onError != null) onError.accept(ex);
                    return null;
                });
    }

    /**
     * Exports the daily items breakdown to CSV asynchronously.
     *
     * @param orders    today's orders (pre-fetched from OrderService)
     * @param onSuccess callback with the saved file path
     * @param onError   callback with the exception on failure
     * @return CompletableFuture for optional chaining
     */
    public CompletableFuture<String> exportDailyItemsAsync(
            List<Order> orders,
            Consumer<String> onSuccess,
            Consumer<Throwable> onError) {

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        LOGGER.info("AsyncReportGenerator: exporting daily items CSV...");
                        String path = reportExporter.exportDailyItemsCSV(orders);
                        ActivityLogger.getInstance().logReportExported("DAILY_ITEMS", path);
                        return path;
                    } catch (IOException e) {
                        throw new RuntimeException("Daily items export failed: " + e.getMessage(), e);
                    }
                }, executor)
                .thenApply(path -> {
                    if (onSuccess != null) onSuccess.accept(path);
                    return path;
                })
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "AsyncReportGenerator: daily items export failed", ex);
                    if (onError != null) onError.accept(ex);
                    return null;
                });
    }

    /**
     * Shuts down the executor service cleanly.
     * Should be called in Application.stop() or on application exit.
     */
    public void shutdown() {
        executor.shutdown();
        LOGGER.info("AsyncReportGenerator executor shut down.");
    }
}
