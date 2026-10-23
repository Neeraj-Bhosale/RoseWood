package com.restaurant.controller;

import com.restaurant.model.Supplier;
import com.restaurant.service.SupplierService;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class SupplierController {

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Integer> colId;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colContact;
    @FXML private TableColumn<Supplier, String> colPhone;
    @FXML private TableColumn<Supplier, String> colEmail;
    @FXML private TableColumn<Supplier, String> colCategory;

    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField categoryField;
    @FXML private TextField addressField;

    private final SupplierService supplierService = new SupplierService();
    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private Supplier selectedSupplier;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("suppliedCategory"));

        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedSupplier = newVal;
                nameField.setText(newVal.getName());
                contactField.setText(newVal.getContactPerson());
                phoneField.setText(newVal.getPhone());
                emailField.setText(newVal.getEmail());
                categoryField.setText(newVal.getSuppliedCategory());
                addressField.setText(newVal.getAddress());
            }
        });

        loadSuppliers();
    }

    private void loadSuppliers() {
        supplierList.clear();
        supplierList.addAll(supplierService.getAllSuppliers());
        supplierTable.setItems(supplierList);
    }

    @FXML
    public void handleAddSupplier() {
        try {
            boolean success = supplierService.addSupplier(
                    nameField.getText(),
                    contactField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    categoryField.getText()
            );

            if (success) {
                AlertUtil.showInfo("Success", "Supplier registered.");
                clearForm();
                loadSuppliers();
            }
        } catch (Exception e) {
            AlertUtil.showError("Save Error", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateSupplier() {
        if (selectedSupplier == null) {
            AlertUtil.showWarning("No Selection", "Select a supplier to update.");
            return;
        }
        try {
            selectedSupplier.setName(nameField.getText());
            selectedSupplier.setContactPerson(contactField.getText());
            selectedSupplier.setPhone(phoneField.getText());
            selectedSupplier.setEmail(emailField.getText());
            selectedSupplier.setAddress(addressField.getText());
            selectedSupplier.setSuppliedCategory(categoryField.getText());

            boolean success = supplierService.updateSupplier(selectedSupplier);
            if (success) {
                AlertUtil.showInfo("Success", "Supplier updated.");
                clearForm();
                loadSuppliers();
            }
        } catch (Exception e) {
            AlertUtil.showError("Update Error", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteSupplier() {
        if (selectedSupplier == null) {
            AlertUtil.showWarning("No Selection", "Select a supplier to delete.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Delete " + selectedSupplier.getName() + "?");
        if (confirm) {
            supplierService.deleteSupplier(selectedSupplier.getSupplierId());
            clearForm();
            loadSuppliers();
        }
    }

    @FXML
    public void clearForm() {
        selectedSupplier = null;
        nameField.clear();
        contactField.clear();
        phoneField.clear();
        emailField.clear();
        categoryField.clear();
        addressField.clear();
        supplierTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleImportDummyData() {
        try {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            int count = dds.importDummySuppliers();
            AlertUtil.showInfo("Suppliers Imported", "Successfully imported " + count + " dummy suppliers.");
            loadSuppliers();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateData() {
        boolean confirm = AlertUtil.showConfirmation(
                "Vacate Suppliers",
                "Are you sure you want to clear all suppliers? Suppliers will become vacant."
        );
        if (confirm) {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            dds.vacateSuppliers();
            clearForm();
            loadSuppliers();
        }
    }
}
