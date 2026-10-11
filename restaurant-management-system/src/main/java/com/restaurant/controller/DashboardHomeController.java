package com.restaurant.controller;

import com.restaurant.service.ReportService;
import com.restaurant.util.CurrencyUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardHomeController {
    private static final Logger LOGGER = Logger.getLogger(DashboardHomeController.class.getName());

    @FXML private Label statRevenueLabel;
    @FXML private Label statOrdersLabel;
    @FXML private Label statTablesLabel;
    @FXML private Label statKitchenLabel;
    @FXML private Label statLowStockLabel;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        loadMetrics();
    }

    @FXML
    public void loadMetrics() {
        try {
            Map<String, Object> metrics = reportService.getTodaySummaryMetrics();
            if (statRevenueLabel != null) {
                double rev = (double) metrics.getOrDefault("todayRevenue", 0.0);
                statRevenueLabel.setText(CurrencyUtil.format(rev));
            }
            if (statOrdersLabel != null) {
                int orders = (int) metrics.getOrDefault("todayOrders", 0);
                statOrdersLabel.setText(String.valueOf(orders));
            }
            if (statTablesLabel != null) {
                int avail = (int) metrics.getOrDefault("availableTables", 0);
                int occ = (int) metrics.getOrDefault("occupiedTables", 0);
                statTablesLabel.setText(avail + " Free / " + occ + " Busy");
            }
            if (statKitchenLabel != null) {
                int kitchen = (int) metrics.getOrDefault("pendingKitchen", 0);
                statKitchenLabel.setText(String.valueOf(kitchen));
            }
            if (statLowStockLabel != null) {
                int lowStock = (int) metrics.getOrDefault("lowStockCount", 0);
                statLowStockLabel.setText(String.valueOf(lowStock));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error updating dashboard metrics", e);
        }
    }

    @FXML
    public void handleNewOrder() {
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().showOrders();
        }
    }

    @FXML
    public void handleKitchen() {
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().showKitchen();
        }
    }
}
