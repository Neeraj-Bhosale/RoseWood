package com.restaurant.util;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;

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
 * FILE HANDLING — Bill Receipt Writer
 *
 * Saves a formatted text receipt for each completed order to the
 * receipts/ directory.  File name: receipts/RECEIPT_<orderNumber>.txt
 *
 * Demonstrates:
 *  - FileWriter + BufferedWriter for sequential text output
 *  - try-with-resources to guarantee stream closure
 *  - Path / Files API (NIO) to create the output directory
 *  - IOException handling with meaningful error propagation
 */
public class BillReceiptWriter {

    private static final Logger LOGGER = Logger.getLogger(BillReceiptWriter.class.getName());
    private static final String RECEIPTS_DIR = "receipts";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Writes the formatted receipt text to a .txt file.
     *
     * @param order          the completed order
     * @param receiptContent the pre-formatted receipt string (from BillingService)
     * @return the absolute path of the saved file, or null on failure
     * @throws IOException if the file cannot be created or written
     */
    public String saveReceipt(Order order, String receiptContent) throws IOException {
        ensureReceiptDirectoryExists();

        String fileName  = buildFileName(order.getOrderNumber());
        String filePath  = RECEIPTS_DIR + "/" + fileName;

        // try-with-resources ensures BufferedWriter is always closed
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(receiptContent);
            writer.newLine();
            writer.write("Saved at: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.flush();
        }
        // IOException propagates to the caller — caller decides how to inform the user

        LOGGER.info("Receipt saved → " + Paths.get(filePath).toAbsolutePath());
        return Paths.get(filePath).toAbsolutePath().toString();
    }

    /**
     * Convenience overload: generates a simple receipt from raw order data
     * without needing a pre-formatted string.
     */
    public String saveSimpleReceipt(Order order) throws IOException {
        String content = buildSimpleReceipt(order);
        return saveReceipt(order, content);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private String buildFileName(String orderNumber) {
        // Sanitise order number so it is safe for use as a filename
        String safe = orderNumber.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return "RECEIPT_" + safe + ".txt";
    }

    private String buildSimpleReceipt(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("           ROSEWOOD — FINE DINING             \n");
        sb.append("==================================================\n");
        sb.append(String.format("Order No  : %s%n",  order.getOrderNumber()));
        sb.append(String.format("Table     : %s%n",
                order.getTableNumber() != null ? order.getTableNumber() : "Takeaway"));
        sb.append(String.format("Customer  : %s%n",
                order.getCustomerName() != null ? order.getCustomerName() : "Walk-in"));
        sb.append(String.format("Date/Time : %s%n",
                order.getOrderDateTime() != null
                        ? order.getOrderDateTime().format(FORMATTER)
                        : LocalDateTime.now().format(FORMATTER)));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-25s %5s %10s%n", "Item", "Qty", "Subtotal"));
        sb.append("--------------------------------------------------\n");

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String name = item.getItemName();
                if (name != null && name.length() > 24) name = name.substring(0, 21) + "...";
                sb.append(String.format("%-25s %5d %10.2f%n",
                        name, item.getQuantity(), item.getSubtotal()));
            }
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-32s : %10.2f%n", "Subtotal",       order.getSubtotal()));
        sb.append(String.format("%-32s : %10.2f%n", "Tax",            order.getTaxAmount()));
        sb.append(String.format("%-32s : %10.2f%n", "Service Charge", order.getServiceCharge()));
        if (order.getDiscountAmount() > 0) {
            sb.append(String.format("%-32s : %10.2f%n", "Discount", -order.getDiscountAmount()));
        }
        sb.append("==================================================\n");
        sb.append(String.format("%-32s : %10.2f%n", "GRAND TOTAL", order.getTotalAmount()));
        sb.append("==================================================\n");
        sb.append("          THANK YOU!  VISIT US AGAIN!            \n");
        sb.append("==================================================\n");
        return sb.toString();
    }

    /** Creates the receipts/ directory if it does not exist yet. */
    private void ensureReceiptDirectoryExists() throws IOException {
        Path dir = Paths.get(RECEIPTS_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            LOGGER.info("BillReceiptWriter: created receipts directory → " + dir.toAbsolutePath());
        }
    }
}
