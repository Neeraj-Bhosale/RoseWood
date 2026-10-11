package com.restaurant.controller;

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

public class BillingController {

    @FXML private TableView<Order> readyOrdersTable;
    @FXML private TableColumn<Order, String> colOrderNumber;
    @FXML private TableColumn<Order, String> colTable;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, OrderStatus> colStatus;
    @FXML private TableColumn<Order, Double> colAmount;

    @FXML private TextField discountField;
    @FXML private TextArea receiptPreviewArea;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTax;
    @FXML private Label lblService;
    @FXML private Label lblTotal;

    private final OrderService orderService = new OrderService();
    private final BillingService billingService = new BillingService();
    private final ObservableList<Order> billableOrders = FXCollections.observableArrayList();
    private Order selectedOrder;

    @FXML
    public void initialize() {
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colAmount.setCellFactory(CurrencyUtil.forTableColumn());

        readyOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Fetch full order with items
                selectedOrder = orderService.getOrderById(newVal.getOrderId());
                updateBillPreview();
            }
        });

        loadBillableOrders();
    }

    private void loadBillableOrders() {
        billableOrders.clear();
        for (Order o : orderService.getAllOrders()) {
            if (o.getStatus() == OrderStatus.SERVED || o.getStatus() == OrderStatus.READY || o.getStatus() == OrderStatus.CONFIRMED) {
                billableOrders.add(o);
            }
        }
        readyOrdersTable.setItems(billableOrders);
    }

    @FXML
    public void handleApplyDiscount() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order to apply discount.");
            return;
        }
        try {
            double discount = Double.parseDouble(discountField.getText().trim());
            if (discount < 0 || discount > selectedOrder.getSubtotal()) {
                AlertUtil.showError("Invalid Discount", "Discount cannot be negative or greater than subtotal.");
                return;
            }
            selectedOrder.setDiscountAmount(discount);
            updateBillPreview();
        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Number", "Please enter a valid numeric discount amount.");
        }
    }

    private void updateBillPreview() {
        if (selectedOrder != null) {
            lblSubtotal.setText(CurrencyUtil.format(selectedOrder.getSubtotal()));
            lblDiscount.setText(CurrencyUtil.format(selectedOrder.getDiscountAmount()));
            lblTax.setText(CurrencyUtil.format(selectedOrder.getTaxAmount()));
            lblService.setText(CurrencyUtil.format(selectedOrder.getServiceCharge()));
            lblTotal.setText(CurrencyUtil.format(selectedOrder.getTotalAmount()));

            String receipt = billingService.generateBillReceipt(selectedOrder);
            receiptPreviewArea.setText(receipt);
        }
    }

    @FXML
    public void handlePrintBill() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Order", "Select an order to generate bill.");
            return;
        }
        AlertUtil.showInfo("Invoice Generated", "Invoice successfully generated for Order #" + selectedOrder.getOrderNumber() + ".\nReady for cashier settlement.");
    }
}
