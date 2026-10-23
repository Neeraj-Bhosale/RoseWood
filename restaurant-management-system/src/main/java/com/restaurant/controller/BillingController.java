package com.restaurant.controller;

// Import required model and service classes.
import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Order;
import com.restaurant.service.BillingService;
import com.restaurant.service.OrderService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * This class controls the billing screen in the JavaFX application.
 * It shows billable orders, calculates totals, applies discounts, and previews receipts.
 */
public class BillingController {

    // Table and columns for showing orders ready for billing.
    @FXML private TableView<Order> readyOrdersTable;
    @FXML private TableColumn<Order, String> colOrderNumber;
    @FXML private TableColumn<Order, String> colTable;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, OrderStatus> colStatus;
    @FXML private TableColumn<Order, Double> colAmount;

    // Input and display fields for bill calculation.
    @FXML private TextField discountField;
    @FXML private TextArea receiptPreviewArea;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTax;
    @FXML private Label lblService;
    @FXML private Label lblTotal;

    // Services used to get orders and generate bills.
    private final OrderService orderService = new OrderService();
    private final BillingService billingService = new BillingService();
    // This list stores the orders shown in the billing table.
    private final ObservableList<Order> billableOrders = FXCollections.observableArrayList();
    // This stores the currently selected order to be billed.
    private Order selectedOrder;

    @FXML
    public void initialize() {
        // Link each table column to a property of the Order object.
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colAmount.setCellFactory(CurrencyUtil.forTableColumn());

        // This listener detects when the user selects a row from the table.
        readyOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Load the full order details from the database.
                selectedOrder = orderService.getOrderById(newVal.getOrderId());
                // Update the bill preview on the screen.
                updateBillPreview();
            }
        });

        // Fill the table with ready orders.
        loadBillableOrders();
    }

    // This method loads all orders that are ready to be billed.
    private void loadBillableOrders() {
        billableOrders.clear();
        for (Order o : orderService.getAllOrders()) {
            // Show orders whose status is SERVED, READY, or CONFIRMED.
            if (o.getStatus() == OrderStatus.SERVED || o.getStatus() == OrderStatus.READY || o.getStatus() == OrderStatus.CONFIRMED) {
                billableOrders.add(o);
            }
        }
        readyOrdersTable.setItems(billableOrders);
    }

    @FXML
    public void handleApplyDiscount() {
        // If no order is selected, stop the action.
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order to apply discount.");
            return;
        }
        try {
            // Read the discount amount from the text field.
            double discount = Double.parseDouble(discountField.getText().trim());
            // Discount cannot be negative or more than the subtotal.
            if (discount < 0 || discount > selectedOrder.getSubtotal()) {
                AlertUtil.showError("Invalid Discount", "Discount cannot be negative or greater than subtotal.");
                return;
            }
            // Save the discount on the selected order.
            selectedOrder.setDiscountAmount(discount);
            // Refresh the bill preview.
            updateBillPreview();
        } catch (NumberFormatException e) {
            // If the user enters letters instead of numbers, show an error.
            AlertUtil.showError("Invalid Number", "Please enter a valid numeric discount amount.");
        }
    }

    // This method updates all labels and the receipt preview.
    private void updateBillPreview() {
        if (selectedOrder != null) {
            // Format and display all values with currency style.
            lblSubtotal.setText(CurrencyUtil.format(selectedOrder.getSubtotal()));
            lblDiscount.setText(CurrencyUtil.format(selectedOrder.getDiscountAmount()));
            lblTax.setText(CurrencyUtil.format(selectedOrder.getTaxAmount()));
            lblService.setText(CurrencyUtil.format(selectedOrder.getServiceCharge()));
            lblTotal.setText(CurrencyUtil.format(selectedOrder.getTotalAmount()));

            // Generate the receipt text for the selected order.
            String receipt = billingService.generateBillReceipt(selectedOrder);
            receiptPreviewArea.setText(receipt);
        }
    }

    @FXML
    public void handlePrintBill() {
        // If no order is selected, do not print anything.
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Order", "Select an order to generate bill.");
            return;
        }
        // Show success message to the cashier after invoice generation.
        AlertUtil.showInfo("Invoice Generated", "Invoice successfully generated for Order #" + selectedOrder.getOrderNumber() + ".\nReady for cashier settlement.");
    }
}
