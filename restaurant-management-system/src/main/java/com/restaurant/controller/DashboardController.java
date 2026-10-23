package com.restaurant.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.restaurant.Main;
import com.restaurant.enums.UserRole;
import com.restaurant.model.User;
import com.restaurant.service.AuthenticationService;
import com.restaurant.service.ReportService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private static DashboardController instance;

    public static DashboardController getInstance() {
        return instance;
    }

    @FXML private Label userGreetingLabel;
    @FXML private Label userRoleBadge;
    @FXML private StackPane contentArea;

    // Stat metric labels
    @FXML private Label statRevenueLabel;
    @FXML private Label statOrdersLabel;
    @FXML private Label statTablesLabel;
    @FXML private Label statKitchenLabel;
    @FXML private Label statLowStockLabel;

    // Nav buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnOrders;
    @FXML private Button btnKitchen;
    @FXML private Button btnTables;
    @FXML private Button btnMenu;
    @FXML private Button btnCustomers;
    @FXML private Button btnReservations;
    @FXML private Button btnInventory;
    @FXML private Button btnSuppliers;
    @FXML private Button btnBilling;
    @FXML private Button btnPayments;
    @FXML private Button btnReports;
    @FXML private Button btnDataManagement;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        instance = this;
        User user = AuthenticationService.getCurrentUser();
        if (user != null) {
            userGreetingLabel.setText("Welcome, " + user.getFullName());
            userRoleBadge.setText(user.getRole().name());
            applyRolePermissions(user.getRole());
        } else {
            AlertUtil.showError("Session expired", "You are not logged in. Please sign in again.");
            Main.loadLoginScreen();
            return;
        }

        loadDashboardMetrics();
    }

    private void applyRolePermissions(UserRole role) {
        if (role == UserRole.KITCHEN_STAFF) {
            if (btnOrders != null) btnOrders.setDisable(true);
            if (btnBilling != null) btnBilling.setDisable(true);
            if (btnPayments != null) btnPayments.setDisable(true);
            if (btnReports != null) btnReports.setDisable(true);
            if (btnDataManagement != null) btnDataManagement.setDisable(true);
        } else if (role == UserRole.WAITER) {
            if (btnBilling != null) btnBilling.setDisable(true);
            if (btnPayments != null) btnPayments.setDisable(true);
            if (btnReports != null) btnReports.setDisable(true);
            if (btnInventory != null) btnInventory.setDisable(true);
            if (btnDataManagement != null) btnDataManagement.setDisable(true);
        } else if (role == UserRole.CASHIER) {
            if (btnMenu != null) btnMenu.setDisable(true);
            if (btnKitchen != null) btnKitchen.setDisable(true);
            if (btnInventory != null) btnInventory.setDisable(true);
            if (btnDataManagement != null) btnDataManagement.setDisable(true);
        }
    }

    public void loadDashboardMetrics() {
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

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Node node = loader.load();
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed loading view: " + fxmlFile, e);
            AlertUtil.showError("Navigation Error", "Could not load view: " + fxmlFile + "\n" + e.getMessage());
        }
    }

    @FXML public void showDashboard() { loadDashboardMetrics(); loadView("dashboard_home.fxml"); }
    @FXML public void showOrders() { loadView("orders.fxml"); }
    @FXML public void showKitchen() { loadView("kitchen.fxml"); }
    @FXML public void showTables() { loadView("tables.fxml"); }
    @FXML public void showMenu() { loadView("menu.fxml"); }
    @FXML public void showCustomers() { loadView("customers.fxml"); }
    @FXML public void showReservations() { loadView("reservations.fxml"); }
    @FXML public void showInventory() { loadView("inventory.fxml"); }
    @FXML public void showSuppliers() { loadView("suppliers.fxml"); }
    @FXML public void showBilling() { loadView("billing.fxml"); }
    @FXML public void showPayments() { loadView("payments.fxml"); }
    @FXML public void showReports() { loadView("reports.fxml"); }
    @FXML public void showDataManagement() { loadView("data_management.fxml"); }

    @FXML
    public void handleLogout() {
        boolean confirm = AlertUtil.showConfirmation("Confirm Logout", "Are you sure you want to log out?");
        if (confirm) {
            new AuthenticationService().logout();
            Main.loadLoginScreen();
        }
    }
}
