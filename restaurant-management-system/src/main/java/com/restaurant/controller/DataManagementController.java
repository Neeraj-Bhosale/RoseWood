package com.restaurant.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.restaurant.model.InventoryLog;
import com.restaurant.service.DummyDataService;
import com.restaurant.service.InventoryService;
import com.restaurant.util.AlertUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DataManagementController {

    @FXML private Label badgeSuppliers;
    @FXML private Label badgeInventory;
    @FXML private Label badgeDishes;
    @FXML private Label badgeCustomers;

    @FXML private TableView<InventoryLog> logsTable;
    @FXML private TableColumn<InventoryLog, Integer> colLogId;
    @FXML private TableColumn<InventoryLog, String> colOrderNumber;
    @FXML private TableColumn<InventoryLog, String> colIngredient;
    @FXML private TableColumn<InventoryLog, Double> colDeducted;
    @FXML private TableColumn<InventoryLog, String> colUnit;
    @FXML private TableColumn<InventoryLog, Double> colPrevious;
    @FXML private TableColumn<InventoryLog, Double> colRemaining;
    @FXML private TableColumn<InventoryLog, String> colTime;
    @FXML private TableColumn<InventoryLog, String> colNotes;

    private final DummyDataService dummyDataService = new DummyDataService();
    private final InventoryService inventoryService = new InventoryService();
    private final ObservableList<InventoryLog> logsList = FXCollections.observableArrayList();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        if (colLogId != null) colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        if (colOrderNumber != null) colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        if (colIngredient != null) colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        if (colDeducted != null) colDeducted.setCellValueFactory(new PropertyValueFactory<>("deductedQuantity"));
        if (colUnit != null) colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        if (colPrevious != null) colPrevious.setCellValueFactory(new PropertyValueFactory<>("previousStock"));
        if (colRemaining != null) colRemaining.setCellValueFactory(new PropertyValueFactory<>("remainingStock"));
        if (colNotes != null) colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        if (colTime != null) {
            colTime.setCellValueFactory(cellData -> {
                var dt = cellData.getValue().getCreatedAt();
                return new SimpleStringProperty(dt != null ? dt.format(TIME_FMT) : "N/A");
            });
        }

        refreshBadges();
        loadLogs();
    }

    public void refreshBadges() {
        int sup = dummyDataService.getSupplierCount();
        int inv = dummyDataService.getInventoryCount();
        int dish = dummyDataService.getDishesCount();
        int cust = dummyDataService.getCustomerCount();

        updateBadge(badgeSuppliers, sup, "Suppliers");
        updateBadge(badgeInventory, inv, "Ingredients");
        updateBadge(badgeDishes, dish, "Dishes");
        updateBadge(badgeCustomers, cust, "Customers");
    }

    private void updateBadge(Label label, int count, String noun) {
        if (label == null) return;
        if (count == 0) {
            label.setText("Vacant (0)");
            label.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 2px 8px; -fx-background-radius: 10px;");
        } else {
            label.setText(count + " " + noun);
            label.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-padding: 2px 8px; -fx-background-radius: 10px;");
        }
    }

    private void loadLogs() {
        if (logsTable == null) return;
        logsList.clear();
        List<InventoryLog> logs = inventoryService.getInventoryLogs(100);
        logsList.addAll(logs);
        logsTable.setItems(logsList);
    }

    @FXML
    public void handleRefresh() {
        refreshBadges();
        loadLogs();
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().loadDashboardMetrics();
        }
    }

    @FXML
    public void handleImportAll() {
        try {
            dummyDataService.importAllDummyData();
            AlertUtil.showInfo("Import Success", "All dummy sample data successfully imported!\n• Tables (T-01 to T-10)\n• 4 Wholesale Suppliers\n• 12 Raw Inventory Ingredients\n• 19 Dishes across 6 Categories with Recipes\n• 5 Sample Diners");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Import Error", e.getMessage());
        }
    }

    @FXML
    public void handleVacateAll() {
        boolean confirm = AlertUtil.showConfirmation(
                "Vacate All Operational Data",
                "Are you sure you want to vacate all operational data (suppliers, inventory, dishes, customers, orders)?\n\nStaff login accounts and tables will be preserved."
        );
        if (confirm) {
            try {
                boolean ok = dummyDataService.vacateAllData();
                if (ok) {
                    AlertUtil.showInfo("Vacated", "Database reset to a vacant clean slate! You can now test adding custom data or re-importing dummy data.");
                    handleRefresh();
                } else {
                    AlertUtil.showError("Vacate Failed", "Could not vacate all data. Check server logs.");
                }
            } catch (Exception e) {
                AlertUtil.showError("Vacate Error", e.getMessage());
            }
        }
    }

    @FXML
    public void handleImportSuppliers() {
        try {
            int count = dummyDataService.importDummySuppliers();
            AlertUtil.showInfo("Suppliers Imported", "Imported " + count + " dummy suppliers.");
            refreshBadges();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateSuppliers() {
        boolean confirm = AlertUtil.showConfirmation("Vacate Suppliers", "Are you sure you want to remove all suppliers?");
        if (confirm) {
            dummyDataService.vacateSuppliers();
            refreshBadges();
        }
    }

    @FXML
    public void handleImportInventory() {
        try {
            int count = dummyDataService.importDummyInventory();
            AlertUtil.showInfo("Inventory Imported", "Imported " + count + " dummy ingredients with stock & threshold.");
            refreshBadges();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateInventory() {
        boolean confirm = AlertUtil.showConfirmation("Vacate Inventory", "Are you sure you want to remove all inventory ingredients and recipes?");
        if (confirm) {
            dummyDataService.vacateInventory();
            refreshBadges();
        }
    }

    @FXML
    public void handleImportDishes() {
        try {
            int count = dummyDataService.importDummyDishes();
            AlertUtil.showInfo("Dishes Imported", "Imported " + count + " dishes with complete ingredient recipes.");
            refreshBadges();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateDishes() {
        boolean confirm = AlertUtil.showConfirmation("Vacate Menu Dishes", "Are you sure you want to remove all menu categories and dishes?");
        if (confirm) {
            dummyDataService.vacateDishes();
            refreshBadges();
        }
    }

    @FXML
    public void handleImportCustomers() {
        try {
            int count = dummyDataService.importDummyCustomers();
            AlertUtil.showInfo("Customers Imported", "Imported " + count + " dummy customers.");
            refreshBadges();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateCustomers() {
        boolean confirm = AlertUtil.showConfirmation("Vacate Customers", "Are you sure you want to remove all customers?");
        if (confirm) {
            dummyDataService.vacateCustomers();
            refreshBadges();
        }
    }
}
