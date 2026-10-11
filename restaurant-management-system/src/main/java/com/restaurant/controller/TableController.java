package com.restaurant.controller;

import com.restaurant.enums.TableStatus;
import com.restaurant.model.RestaurantTable;
import com.restaurant.service.TableService;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableController {

    @FXML private TableView<RestaurantTable> tableTable;
    @FXML private TableColumn<RestaurantTable, Integer> colId;
    @FXML private TableColumn<RestaurantTable, String> colNumber;
    @FXML private TableColumn<RestaurantTable, Integer> colCapacity;
    @FXML private TableColumn<RestaurantTable, String> colLocation;
    @FXML private TableColumn<RestaurantTable, TableStatus> colStatus;

    @FXML private TextField tableNumberField;
    @FXML private TextField capacityField;
    @FXML private TextField locationField;
    @FXML private ComboBox<TableStatus> statusComboBox;

    private final TableService tableService = new TableService();
    private final ObservableList<RestaurantTable> tableList = FXCollections.observableArrayList();
    private RestaurantTable selectedTable;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusComboBox.setItems(FXCollections.observableArrayList(TableStatus.values()));
        statusComboBox.setValue(TableStatus.AVAILABLE);

        tableTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTable = newVal;
                tableNumberField.setText(newVal.getTableNumber());
                capacityField.setText(String.valueOf(newVal.getCapacity()));
                locationField.setText(newVal.getLocation());
                statusComboBox.setValue(newVal.getStatus());
            }
        });

        loadTables();
    }

    private void loadTables() {
        tableList.clear();
        tableList.addAll(tableService.getAllTables());
        tableTable.setItems(tableList);
    }

    @FXML
    public void handleAddTable() {
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            boolean success = tableService.addTable(tableNumberField.getText(), capacity, locationField.getText());
            if (success) {
                AlertUtil.showInfo("Success", "Table created successfully.");
                clearForm();
                loadTables();
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Input", "Capacity must be an integer.");
        } catch (Exception e) {
            AlertUtil.showError("Creation Failed", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateTable() {
        if (selectedTable == null) {
            AlertUtil.showWarning("No Selection", "Please select a table to update.");
            return;
        }
        try {
            selectedTable.setTableNumber(tableNumberField.getText());
            selectedTable.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            selectedTable.setLocation(locationField.getText());
            selectedTable.setStatus(statusComboBox.getValue());

            boolean success = tableService.updateTable(selectedTable);
            if (success) {
                AlertUtil.showInfo("Success", "Table updated successfully.");
                clearForm();
                loadTables();
            }
        } catch (Exception e) {
            AlertUtil.showError("Update Failed", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteTable() {
        if (selectedTable == null) {
            AlertUtil.showWarning("No Selection", "Please select a table to delete.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Delete table " + selectedTable.getTableNumber() + "?");
        if (confirm) {
            boolean success = tableService.deleteTable(selectedTable.getTableId());
            if (success) {
                AlertUtil.showInfo("Success", "Table removed.");
                clearForm();
                loadTables();
            }
        }
    }

    @FXML
    public void handleSetAvailable() {
        if (selectedTable != null) {
            tableService.changeTableStatus(selectedTable.getTableId(), TableStatus.AVAILABLE);
            loadTables();
        }
    }

    @FXML
    public void handleSetCleaning() {
        if (selectedTable != null) {
            tableService.changeTableStatus(selectedTable.getTableId(), TableStatus.CLEANING);
            loadTables();
        }
    }

    @FXML
    public void clearForm() {
        selectedTable = null;
        tableNumberField.clear();
        capacityField.clear();
        locationField.clear();
        statusComboBox.setValue(TableStatus.AVAILABLE);
        tableTable.getSelectionModel().clearSelection();
    }
}
