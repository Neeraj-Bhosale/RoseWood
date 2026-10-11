package com.restaurant.service;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Order;
import com.restaurant.util.ActivityLogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MULTI-THREADING — Kitchen Order Processor
 *
 * Runs a dedicated background daemon thread that continuously polls a
 * BlockingQueue<Order> for new kitchen orders and "processes" each one
 * (simulates cooking by sleeping, then marks order READY).
 *
 * Demonstrates:
 *  - Runnable / Thread lifecycle (start, interrupt, shutdown)
 *  - BlockingQueue for producer-consumer pattern (thread-safe handoff)
 *  - AtomicBoolean as a volatile shutdown flag
 *  - InterruptedException handling inside a worker thread
 *  - Daemon threads (does not prevent JVM exit)
 *
 * Usage:
 *   KitchenOrderProcessor processor = KitchenOrderProcessor.getInstance();
 *   processor.start();
 *   processor.submitOrder(order);   // called by OrderService after saving
 */
public class KitchenOrderProcessor implements Runnable {

    private static final Logger LOGGER =
            Logger.getLogger(KitchenOrderProcessor.class.getName());

    /** Average simulated cooking time in milliseconds (3 seconds). */
    private static final long COOK_TIME_MS = 3_000L;

    // Singleton instance
    private static KitchenOrderProcessor instance;

    /** Thread-safe queue: OrderService puts orders in; processor takes them out. */
    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    /** Volatile flag — written by main thread, read by worker thread. */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /** The background worker thread. */
    private Thread workerThread;

    /** Reference to OrderService for status updates. */
    private final OrderService orderService;

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    private KitchenOrderProcessor() {
        this.orderService = new OrderService();
    }

    public static synchronized KitchenOrderProcessor getInstance() {
        if (instance == null) {
            instance = new KitchenOrderProcessor();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Thread lifecycle
    // -----------------------------------------------------------------------

    /**
     * Starts the background kitchen worker thread.
     * Calling start() when already running is a no-op.
     */
    public synchronized void start() {
        if (running.get()) {
            LOGGER.info("KitchenOrderProcessor already running.");
            return;
        }
        running.set(true);
        workerThread = new Thread(this, "Kitchen-Worker-Thread");
        workerThread.setDaemon(true); // JVM can exit even if this thread is still waiting
        workerThread.start();
        LOGGER.info("KitchenOrderProcessor started — kitchen worker thread is live.");
        ActivityLogger.getInstance().logInfo("Kitchen worker thread started.");
    }

    /**
     * Signals the worker thread to stop after finishing the current order.
     * Interrupts the thread if it is blocked on queue.take().
     */
    public synchronized void stop() {
        running.set(false);
        if (workerThread != null) {
            workerThread.interrupt(); // unblock queue.take()
        }
        LOGGER.info("KitchenOrderProcessor stop requested.");
    }

    /**
     * Submits an order to the processing queue.
     * Called by OrderService after the order is persisted to the DB.
     *
     * @param order the newly created order to be sent to the kitchen
     */
    public void submitOrder(Order order) {
        if (order == null) return;
        queue.offer(order); // non-blocking; LinkedBlockingQueue is unbounded
        LOGGER.info("Order submitted to kitchen queue: " + order.getOrderNumber()
                    + " | Queue size: " + queue.size());
    }

    /** Returns the number of orders currently waiting in the kitchen queue. */
    public int getPendingCount() {
        return queue.size();
    }

    // -----------------------------------------------------------------------
    // Runnable — background processing logic (MULTI-THREADING)
    // -----------------------------------------------------------------------

    /**
     * The worker thread body.
     * Blocks on queue.take() until an order arrives, then simulates cooking
     * and updates the order status to READY.
     *
     * InterruptedException is caught properly so the thread exits cleanly
     * when stop() is called.
     */
    @Override
    public void run() {
        LOGGER.info("Kitchen worker thread started — waiting for orders...");

        while (running.get()) {
            try {
                // BLOCKING CALL: waits here until an order is available
                Order order = queue.take();

                LOGGER.info("Kitchen: picked up order " + order.getOrderNumber()
                        + " — cooking started.");
                ActivityLogger.getInstance().logInfo(
                        "Kitchen processing: " + order.getOrderNumber());

                // Simulate cooking time (demonstrates Thread.sleep in a worker)
                simulateCooking(order);

                // Mark the order as READY
                updateOrderReady(order);

            } catch (InterruptedException e) {
                // Restore the interrupted flag — good practice
                Thread.currentThread().interrupt();
                LOGGER.info("Kitchen worker thread interrupted — shutting down gracefully.");
                break; // exit the loop and let the thread terminate
            } catch (Exception e) {
                // Never let an unexpected exception kill the worker thread silently
                LOGGER.log(Level.SEVERE,
                        "Unexpected error in kitchen worker thread", e);
                ActivityLogger.getInstance().logError(
                        "KitchenOrderProcessor", e.getMessage());
            }
        }

        LOGGER.info("Kitchen worker thread has stopped.");
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Simulates the time it takes to prepare an order.
     * In a real system this would trigger a kitchen display update.
     *
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private void simulateCooking(Order order) throws InterruptedException {
        // Adjust cooking time slightly per item to feel realistic
        int itemCount = (order.getItems() != null) ? order.getItems().size() : 1;
        long cookTime = COOK_TIME_MS + (itemCount * 500L);

        LOGGER.info(String.format("Kitchen: cooking order %s (%d items) for ~%d ms...",
                order.getOrderNumber(), itemCount, cookTime));

        Thread.sleep(cookTime); // demonstrates Thread.sleep inside a worker thread
    }

    /** Marks the order as READY and logs the event. */
    private void updateOrderReady(Order order) {
        try {
            boolean updated = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.READY);
            if (updated) {
                LOGGER.info("Kitchen: Order " + order.getOrderNumber() + " is READY!");
                ActivityLogger.getInstance().logInfo(
                        "Order READY: " + order.getOrderNumber());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                    "Could not update order status to READY for: " + order.getOrderNumber(), e);
        }
    }
}
