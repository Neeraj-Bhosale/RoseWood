package com.restaurant.controller;

import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.model.CardPayment;
import com.restaurant.model.CashPayment;
import com.restaurant.model.Order;
import com.restaurant.model.Payment;
import com.restaurant.model.UPIPayment;
import com.restaurant.model.User;
import com.restaurant.service.AuthenticationService;
import com.restaurant.service.OrderService;
import com.restaurant.service.PaymentService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class PaymentController {

    @FXML private TableView<Order> unpaidOrdersTable;
    @FXML private TableColumn<Order, String> colOrderNumber;
    @FXML private TableColumn<Order, String> colTable;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, Double> colTotal;

    @FXML private ComboBox<PaymentMethod> paymentMethodCombo;
    @FXML private Label selectedOrderLabel;
    @FXML private Label amountDueLabel;

    // Payment specific panels
    @FXML private VBox cashPanel;
    @FXML private TextField cashTenderedField;
    @FXML private Label changeDueLabel;

    @FXML private VBox upiPanel;
    @FXML private TextField upiIdField;
    @FXML private TextField upiTxnRefField;

    @FXML private VBox cardPanel;
    @FXML private TextField cardLastFourField;
    @FXML private TextField cardAuthCodeField;

    @FXML private TextArea receiptOutputArea;

    private final OrderService orderService = new OrderService();
    private final PaymentService paymentService = new PaymentService();
    private final ObservableList<Order> unpaidList = FXCollections.observableArrayList();
    private Order selectedOrder;

    @FXML
    public void initialize() {
        User currentUser = AuthenticationService.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Access denied", "You must log in to access the payment screen.");
            return;
        }

        try {
            AuthenticationService.requireAnyRole(com.restaurant.enums.UserRole.ADMIN,
                    com.restaurant.enums.UserRole.MANAGER,
                    com.restaurant.enums.UserRole.CASHIER);
        } catch (SecurityException ex) {
            AlertUtil.showError("Access denied", ex.getMessage());
            if (unpaidOrdersTable != null) unpaidOrdersTable.setDisable(true);
            if (paymentMethodCombo != null) paymentMethodCombo.setDisable(true);
            if (cashTenderedField != null) cashTenderedField.setDisable(true);
            if (upiIdField != null) upiIdField.setDisable(true);
            if (cardLastFourField != null) cardLastFourField.setDisable(true);
            return;
        }

        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotal.setCellFactory(CurrencyUtil.forTableColumn());

        paymentMethodCombo.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        paymentMethodCombo.setValue(PaymentMethod.CASH);

        paymentMethodCombo.valueProperty().addListener((obs, oldVal, newVal) -> togglePaymentFields(newVal));

        unpaidOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedOrder = orderService.getOrderById(newVal.getOrderId());
                selectedOrderLabel.setText(selectedOrder.getOrderNumber() + " (Table " + (selectedOrder.getTableNumber() != null ? selectedOrder.getTableNumber() : "Direct") + ")");
                amountDueLabel.setText(CurrencyUtil.format(selectedOrder.getTotalAmount()));
                cashTenderedField.setText(String.format("%.2f", selectedOrder.getTotalAmount()));
            }
        });

        togglePaymentFields(PaymentMethod.CASH);
        loadUnpaidOrders();
    }

    private void togglePaymentFields(PaymentMethod method) {
        if (cashPanel != null) cashPanel.setVisible(method == PaymentMethod.CASH);
        if (upiPanel != null) upiPanel.setVisible(method == PaymentMethod.UPI);
        if (cardPanel != null) cardPanel.setVisible(method == PaymentMethod.CREDIT_CARD || method == PaymentMethod.DEBIT_CARD);
    }

    private void loadUnpaidOrders() {
        unpaidList.clear();
        for (Order o : orderService.getAllOrders()) {
            if (o.getStatus() != OrderStatus.COMPLETED && o.getStatus() != OrderStatus.CANCELLED) {
                unpaidList.add(o);
            }
        }
        unpaidOrdersTable.setItems(unpaidList);
    }

    @FXML
    public void handleProcessPayment() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("Select Order", "Please select an order to process payment.");
            return;
        }

        PaymentMethod method = paymentMethodCombo.getValue();
        User cashier = AuthenticationService.getCurrentUser();
        int cashierId = cashier != null ? cashier.getUserId() : 1;
        String invoiceNumber = "INV-" + System.currentTimeMillis() % 10000000;
        Payment payment;

        // Polymorphic instantiation based on chosen channel
        switch (method) {
            case CASH -> {
                double tendered = Double.parseDouble(cashTenderedField.getText().trim());
                payment = new CashPayment(selectedOrder.getOrderId(), invoiceNumber, selectedOrder.getTotalAmount(), cashierId, tendered);
            }
            case UPI -> {
                String upi = upiIdField.getText().trim();
                String ref = upiTxnRefField.getText().trim();
                payment = new UPIPayment(selectedOrder.getOrderId(), invoiceNumber, selectedOrder.getTotalAmount(), cashierId, upi, ref);
            }
            case CREDIT_CARD, DEBIT_CARD -> {
                String lastFour = cardLastFourField.getText().trim();
                String auth = cardAuthCodeField.getText().trim();
                payment = new CardPayment(selectedOrder.getOrderId(), invoiceNumber, selectedOrder.getTotalAmount(), method, cashierId, lastFour, auth);
            }
            default -> payment = new CashPayment(selectedOrder.getOrderId(), invoiceNumber, selectedOrder.getTotalAmount(), cashierId, selectedOrder.getTotalAmount());
        }

        try {
            boolean success = paymentService.processAndFinalizePayment(payment, selectedOrder);
            if (success) {
                String receipt = payment.generateReceipt(selectedOrder);
                receiptOutputArea.setText(receipt);
                AlertUtil.showInfo("Payment Successful", "Payment completed successfully!\n" + receipt);
                loadUnpaidOrders();
                selectedOrder = null;
                selectedOrderLabel.setText("None");
                amountDueLabel.setText(CurrencyUtil.format(0));
            }
        } catch (Exception e) {
            AlertUtil.showError("Payment Failed", e.getMessage());
        }
    }
}
