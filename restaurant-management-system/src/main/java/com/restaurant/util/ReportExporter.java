package com.restaurant.util;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FILE HANDLING — CSV Report Exporter
 *
 * Exports sales and order data to CSV files in the reports/ directory.
 * Demonstrates:
 *  - Writing CSV-formatted text with PrintWriter/BufferedWriter
 *  - NIO Paths/Files for directory management
 *  - try-with-resources for safe resource cleanup
 *  - Exception handling with meaningful error messages
 */
public class ReportExporter {

    private static final Logger LOGGER = Logger.getLogger(ReportExporter.class.getName());
    private static final String REPORTS_DIR = "reports";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // -----------------------------------------------------------------------
    // Public export methods
    // -----------------------------------------------------------------------

    /**
     * Exports a list of orders to a CSV file.
     * CSV columns: OrderNumber, Table, Customer, Date, Items, Total, Status
     *
     * @param orders   list of orders to export
     * @param filename desired file name (without extension)
     * @return absolute path of created file
     * @throws IOException if the file cannot be written
     */
    public String exportOrdersToCSV(List<Order> orders, String filename) throws IOException {
        ensureReportsDirectoryExists();
        String filePath = REPORTS_DIR + "/" + sanitise(filename) + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // Header row
            writer.write("Order Number,Table,Customer,Date,Item Count,Total (INR),Status");
            writer.newLine();

            // Data rows
            for (Order order : orders) {
                int itemCount = (order.getItems() != null) ? order.getItems().size() : 0;
                String line = String.join(",",
                        escapeCsv(order.getOrderNumber()),
                        escapeCsv(order.getTableNumber() != null ? order.getTableNumber() : "Takeaway"),
                        escapeCsv(order.getCustomerName() != null ? order.getCustomerName() : "Walk-in"),
                        escapeCsv(order.getOrderDateTime() != null
                                ? order.getOrderDateTime().format(DATE_FORMATTER) : ""),
                        String.valueOf(itemCount),
                        String.format("%.2f", order.getTotalAmount()),
                        escapeCsv(order.getStatus() != null ? order.getStatus().name() : "")
                );
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }

        String absolutePath = Paths.get(filePath).toAbsolutePath().toString();
        LOGGER.info("Orders CSV exported → " + absolutePath);
        return absolutePath;
    }

    /**
     * Exports a category-wise sales summary to CSV.
     * CSV columns: Category, Total Sales (INR)
     *
     * @param salesByCategory map of category → total sales value
     * @return absolute path of created file
     * @throws IOException if the file cannot be written
     */
    public String exportSalesByCategoryCSV(Map<String, Double> salesByCategory) throws IOException {
        ensureReportsDirectoryExists();
        String timestamp = java.time.LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String filePath  = REPORTS_DIR + "/sales_by_category_" + timestamp + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("Category,Total Sales (INR)");
            writer.newLine();
            for (Map.Entry<String, Double> entry : salesByCategory.entrySet()) {
                writer.write(escapeCsv(entry.getKey()) + "," +
                             String.format("%.2f", entry.getValue()));
                writer.newLine();
            }
            writer.flush();
        }

        String absolutePath = Paths.get(filePath).toAbsolutePath().toString();
        LOGGER.info("Sales-by-category CSV exported → " + absolutePath);
        return absolutePath;
    }

    /**
     * Exports today's detailed order items to CSV — useful for kitchen/manager review.
     * CSV columns: OrderNumber, ItemName, Quantity, UnitPrice, Subtotal
     *
     * @param orders list of today's orders
     * @return absolute path of created file
     * @throws IOException if the file cannot be written
     */
    public String exportDailyItemsCSV(List<Order> orders) throws IOException {
        ensureReportsDirectoryExists();
        String today    = LocalDate.now().format(DATE_FORMATTER);
        String filePath = REPORTS_DIR + "/daily_items_" + today + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("Order Number,Item Name,Quantity,Unit Price (INR),Subtotal (INR)");
            writer.newLine();

            for (Order order : orders) {
                if (order.getItems() == null) continue;
                for (OrderItem item : order.getItems()) {
                    String line = String.join(",",
                            escapeCsv(order.getOrderNumber()),
                            escapeCsv(item.getItemName()),
                            String.valueOf(item.getQuantity()),
                            String.format("%.2f", item.getUnitPrice()),
                            String.format("%.2f", item.getSubtotal())
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.flush();
        }

        String absolutePath = Paths.get(filePath).toAbsolutePath().toString();
        LOGGER.info("Daily items CSV exported → " + absolutePath);
        return absolutePath;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Creates the reports/ directory if it does not exist. */
    private void ensureReportsDirectoryExists() throws IOException {
        Path dir = Paths.get(REPORTS_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            LOGGER.info("ReportExporter: created reports directory → " + dir.toAbsolutePath());
        }
    }

    /** Wraps a field in double quotes and escapes any internal double quotes. */
    private String escapeCsv(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /** Removes characters that are unsafe in file names. */
    private String sanitise(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
