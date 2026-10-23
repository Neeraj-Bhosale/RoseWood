package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.service.CustomerService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, Integer> colPoints;
    @FXML private TableColumn<Customer, Double> colSpent;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField searchField;

    private final CustomerService customerService = new CustomerService();
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Customer selectedCustomer;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));
        colSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        colSpent.setCellFactory(CurrencyUtil.forTableColumn());

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedCustomer = newVal;
                nameField.setText(newVal.getName());
                phoneField.setText(newVal.getPhone());
                emailField.setText(newVal.getEmail());
                addressField.setText(newVal.getAddress());
            }
        });

        loadCustomers();
    }

    private void loadCustomers() {
        customerList.clear();
        customerList.addAll(customerService.getAllCustomers());
        customerTable.setItems(customerList);
    }

    @FXML
    public void handleAddCustomer() {
        try {
            boolean success = customerService.registerCustomer(
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText()
            );
            if (success) {
                AlertUtil.showInfo("Success", "Customer registered successfully!");
                clearForm();
                loadCustomers();
            }
        } catch (Exception e) {
            AlertUtil.showError("Registration Failed", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateCustomer() {
        if (selectedCustomer == null) {
            AlertUtil.showWarning("No Selection", "Please select a customer to update.");
            return;
        }
        try {
            selectedCustomer.setName(nameField.getText());
            selectedCustomer.setPhone(phoneField.getText());
            selectedCustomer.setEmail(emailField.getText());
            selectedCustomer.setAddress(addressField.getText());

            boolean success = customerService.updateCustomer(selectedCustomer);
            if (success) {
                AlertUtil.showInfo("Success", "Customer updated successfully!");
                clearForm();
                loadCustomers();
            }
        } catch (Exception e) {
            AlertUtil.showError("Update Failed", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteCustomer() {
        if (selectedCustomer == null) {
            AlertUtil.showWarning("No Selection", "Please select a customer to delete.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete customer: " + selectedCustomer.getName() + "?");
        if (confirm) {
            boolean success = customerService.deleteCustomer(selectedCustomer.getCustomerId());
            if (success) {
                AlertUtil.showInfo("Success", "Customer removed.");
                clearForm();
                loadCustomers();
            }
        }
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            customerTable.setItems(customerList);
            return;
        }
        String q = query.toLowerCase().trim();
        ObservableList<Customer> filtered = FXCollections.observableArrayList();
        for (Customer c : customerList) {
            if (c.getName().toLowerCase().contains(q) || c.getPhone().contains(q)) {
                filtered.add(c);
            }
        }
        customerTable.setItems(filtered);
    }

    @FXML
    public void clearForm() {
        selectedCustomer = null;
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        customerTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleImportDummyData() {
        try {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            int count = dds.importDummyCustomers();
            AlertUtil.showInfo("Customers Imported", "Successfully imported " + count + " dummy customers.");
            loadCustomers();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateData() {
        boolean confirm = AlertUtil.showConfirmation(
                "Vacate Customers",
                "Are you sure you want to clear all customers? Customers list will become vacant."
        );
        if (confirm) {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            dds.vacateCustomers();
            clearForm();
            loadCustomers();
        }
    }
}
