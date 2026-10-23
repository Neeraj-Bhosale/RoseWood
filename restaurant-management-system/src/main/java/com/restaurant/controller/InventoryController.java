package com.restaurant.controller;

import com.restaurant.model.InventoryItem;
import com.restaurant.model.Supplier;
import com.restaurant.service.InventoryService;
import com.restaurant.service.SupplierService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class InventoryController {

    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, Integer> colId;
    @FXML private TableColumn<InventoryItem, String> colName;
    @FXML private TableColumn<InventoryItem, String> colCategory;
    @FXML private TableColumn<InventoryItem, Double> colQty;
    @FXML private TableColumn<InventoryItem, String> colUnit;
    @FXML private TableColumn<InventoryItem, Double> colMinStock;
    @FXML private TableColumn<InventoryItem, Double> colUnitCost;
    @FXML private TableColumn<InventoryItem, String> colSupplier;

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField quantityField;
    @FXML private TextField unitField;
    @FXML private TextField minStockField;
    @FXML private TextField unitCostField;
    @FXML private ComboBox<Supplier> supplierCombo;

    @FXML private TextField adjustQtyField;

    private final InventoryService inventoryService = new InventoryService();
    private final SupplierService supplierService = new SupplierService();
    private final ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();
    private InventoryItem selectedItem;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("inventoryId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colMinStock.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
        colUnitCost.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        colUnitCost.setCellFactory(CurrencyUtil.forTableColumn());
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        loadSuppliers();

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedItem = newVal;
                nameField.setText(newVal.getIngredientName());
                categoryField.setText(newVal.getCategory());
                quantityField.setText(String.valueOf(newVal.getQuantity()));
                unitField.setText(newVal.getUnit());
                minStockField.setText(String.valueOf(newVal.getMinimumStock()));
                unitCostField.setText(String.valueOf(newVal.getUnitCost()));
                if (newVal.getSupplierId() != null) {
                    for (Supplier s : supplierCombo.getItems()) {
                        if (s.getSupplierId() == newVal.getSupplierId()) {
                            supplierCombo.setValue(s);
                            break;
                        }
                    }
                }
            }
        });

        loadInventory();
    }

    private void loadSuppliers() {
        supplierCombo.setItems(FXCollections.observableArrayList(supplierService.getAllSuppliers()));
    }

    private void loadInventory() {
        inventoryList.clear();
        inventoryList.addAll(inventoryService.getAllInventory());
        inventoryTable.setItems(inventoryList);
    }

    @FXML
    public void handleAddItem() {
        try {
            Supplier s = supplierCombo.getValue();
            Integer sId = s != null ? s.getSupplierId() : null;
            double qty = Double.parseDouble(quantityField.getText().trim());
            double min = Double.parseDouble(minStockField.getText().trim());
            double cost = Double.parseDouble(unitCostField.getText().trim());

            boolean success = inventoryService.addInventoryItem(
                    nameField.getText(),
                    categoryField.getText(),
                    qty,
                    unitField.getText(),
                    min,
                    cost,
                    sId
            );

            if (success) {
                AlertUtil.showInfo("Success", "Inventory item added.");
                clearForm();
                loadInventory();
            }
        } catch (Exception e) {
            AlertUtil.showError("Save Error", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateItem() {
        if (selectedItem == null) {
            AlertUtil.showWarning("No Selection", "Select an item to update.");
            return;
        }
        try {
            Supplier s = supplierCombo.getValue();
            selectedItem.setIngredientName(nameField.getText());
            selectedItem.setCategory(categoryField.getText());
            selectedItem.setQuantity(Double.parseDouble(quantityField.getText().trim()));
            selectedItem.setUnit(unitField.getText());
            selectedItem.setMinimumStock(Double.parseDouble(minStockField.getText().trim()));
            selectedItem.setUnitCost(Double.parseDouble(unitCostField.getText().trim()));
            selectedItem.setSupplierId(s != null ? s.getSupplierId() : null);

            boolean success = inventoryService.updateInventoryItem(selectedItem);
            if (success) {
                AlertUtil.showInfo("Success", "Inventory updated.");
                clearForm();
                loadInventory();
            }
        } catch (Exception e) {
            AlertUtil.showError("Update Error", e.getMessage());
        }
    }

    @FXML
    public void handleQuickStockAdjust() {
        if (selectedItem == null) {
            AlertUtil.showWarning("No Selection", "Select an item to adjust stock.");
            return;
        }
        try {
            double addQty = Double.parseDouble(adjustQtyField.getText().trim());
            double newQty = Math.max(0, selectedItem.getQuantity() + addQty);
            inventoryService.updateStockQuantity(selectedItem.getInventoryId(), newQty);
            adjustQtyField.clear();
            loadInventory();
        } catch (Exception e) {
            AlertUtil.showError("Adjustment Error", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteItem() {
        if (selectedItem == null) {
            AlertUtil.showWarning("No Selection", "Select an item to delete.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Delete " + selectedItem.getIngredientName() + "?");
        if (confirm) {
            inventoryService.deleteInventoryItem(selectedItem.getInventoryId());
            clearForm();
            loadInventory();
        }
    }

    @FXML
    public void handleShowLowStockOnly() {
        List<InventoryItem> low = inventoryService.getLowStockItems();
        inventoryTable.setItems(FXCollections.observableArrayList(low));
    }

    @FXML
    public void handleShowAllSorted() {
        List<InventoryItem> sorted = inventoryService.getInventorySortedByStockAscending();
        inventoryTable.setItems(FXCollections.observableArrayList(sorted));
    }

    @FXML
    public void clearForm() {
        selectedItem = null;
        nameField.clear();
        categoryField.clear();
        quantityField.clear();
        unitField.clear();
        minStockField.clear();
        unitCostField.clear();
        adjustQtyField.clear();
        supplierCombo.getSelectionModel().clearSelection();
        inventoryTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleImportDummyData() {
        try {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            int count = dds.importDummyInventory();
            AlertUtil.showInfo("Dummy Inventory Imported", "Successfully imported " + count + " raw inventory items.");
            loadSuppliers();
            loadInventory();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateData() {
        boolean confirm = AlertUtil.showConfirmation(
                "Vacate Inventory",
                "Are you sure you want to clear all inventory items? The inventory will become vacant."
        );
        if (confirm) {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            dds.vacateInventory();
            clearForm();
            loadInventory();
        }
    }
}
