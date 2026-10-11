package com.restaurant.service;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.util.ActivityLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MULTI-THREADING — Database Health Monitor
 *
 * Uses a ScheduledExecutorService to ping the MySQL database every 30 seconds
 * on a dedicated background thread.  Logs a warning if connectivity is lost
 * and attempts to reconnect automatically.
 *
 * Demonstrates:
 *  - ScheduledExecutorService (Executors.newSingleThreadScheduledExecutor)
 *  - scheduleAtFixedRate() — periodic recurring task
 *  - Callable/Runnable submitted to an executor
 *  - Graceful shutdown of an ExecutorService (shutdown + awaitTermination)
 *  - Thread naming for easier debugging
 */
public class DatabaseHealthMonitor {

    private static final Logger LOGGER =
            Logger.getLogger(DatabaseHealthMonitor.class.getName());

    /** How often to ping the database (in seconds). */
    private static final long PING_INTERVAL_SECONDS = 30L;

    /** How long to wait before the first ping after startup (in seconds). */
    private static final long INITIAL_DELAY_SECONDS  = 10L;

    // Singleton
    private static DatabaseHealthMonitor instance;

    /** Single-thread scheduled executor — runs the health-check task. */
    private final ScheduledExecutorService scheduler;

    /** Handle to the scheduled future so we can cancel it if needed. */
    private ScheduledFuture<?> scheduledFuture;

    /** Tracks consecutive failures for escalated logging. */
    private int consecutiveFailures = 0;

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    private DatabaseHealthMonitor() {
        // Name the thread so it appears clearly in thread dumps
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread t = new Thread(runnable, "DB-Health-Monitor-Thread");
            t.setDaemon(true); // allow JVM to exit without waiting for this thread
            return t;
        });
    }

    public static synchronized DatabaseHealthMonitor getInstance() {
        if (instance == null) {
            instance = new DatabaseHealthMonitor();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Monitor lifecycle
    // -----------------------------------------------------------------------

    /**
     * Starts the periodic health-check task.
     * The first check runs after INITIAL_DELAY_SECONDS seconds.
     * Subsequent checks run every PING_INTERVAL_SECONDS seconds.
     */
    public void startMonitoring() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            LOGGER.info("DatabaseHealthMonitor is already running.");
            return;
        }

        scheduledFuture = scheduler.scheduleAtFixedRate(
                this::performHealthCheck,
                INITIAL_DELAY_SECONDS,
                PING_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );

        LOGGER.info(String.format(
                "DatabaseHealthMonitor started — pinging DB every %d seconds.",
                PING_INTERVAL_SECONDS));
        ActivityLogger.getInstance().logInfo("DB health monitor started.");
    }

    /**
     * Stops monitoring and shuts down the executor cleanly.
     * Waits up to 5 seconds for the current ping to finish before forcing shutdown.
     */
    public void stopMonitoring() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false); // do not interrupt if running
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // force if it didn't finish in time
                LOGGER.warning("DatabaseHealthMonitor forced shutdown after timeout.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
        LOGGER.info("DatabaseHealthMonitor stopped.");
        ActivityLogger.getInstance().logInfo("DB health monitor stopped.");
    }

    // -----------------------------------------------------------------------
    // The health-check task (runs on the scheduled executor thread)
    // -----------------------------------------------------------------------

    /**
     * Performs a single database connectivity check.
     * This method is submitted to and executed by the ScheduledExecutorService —
     * it runs on the "DB-Health-Monitor-Thread", NOT the JavaFX thread.
     *
     * Exception handling: any unchecked exception must be caught here;
     * if it escapes a ScheduledFuture task, the scheduler silently stops
     * scheduling future runs.
     */
    private void performHealthCheck() {
        try {
            boolean ok = DatabaseConfig.getInstance().testConnection();

            if (ok) {
                if (consecutiveFailures > 0) {
                    LOGGER.info("DatabaseHealthMonitor: DB connection RESTORED after "
                            + consecutiveFailures + " failure(s).");
                    ActivityLogger.getInstance().logInfo("DB connection restored.");
                } else {
                    LOGGER.fine("DatabaseHealthMonitor: DB ping OK.");
                }
                consecutiveFailures = 0;

            } else {
                consecutiveFailures++;
                String msg = "DatabaseHealthMonitor: DB ping FAILED (consecutive failures: "
                        + consecutiveFailures + ")";
                LOGGER.warning(msg);
                ActivityLogger.getInstance().logError("DBHealthMonitor", msg);

                if (consecutiveFailures >= 3) {
                    LOGGER.severe("DatabaseHealthMonitor: 3+ consecutive DB failures — "
                            + "application may be unable to serve requests.");
                }
            }

        } catch (Exception e) {
            // Catch ALL exceptions — a ScheduledTask must never throw unchecked
            consecutiveFailures++;
            LOGGER.log(Level.SEVERE,
                    "DatabaseHealthMonitor: unexpected error during health check", e);
            ActivityLogger.getInstance().logError("DBHealthMonitor", e.getMessage());
        }
    }

    /** Returns how many consecutive ping failures have been recorded. */
    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }
}
