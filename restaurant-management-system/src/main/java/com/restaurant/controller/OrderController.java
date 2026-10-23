package com.restaurant.controller;

import java.util.ArrayList;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Customer;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.User;
import com.restaurant.service.AuthenticationService;
import com.restaurant.service.CustomerService;
import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrderController {

    @FXML private ComboBox<RestaurantTable> tableComboBox;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<MenuItem> menuItemComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextField itemInstructionsField;
    @FXML private TextArea orderNotesArea;

    @FXML private TableView<OrderItem> currentOrderItemsTable;
    @FXML private TableColumn<OrderItem, String> colItemName;
    @FXML private TableColumn<OrderItem, Integer> colItemQty;
    @FXML private TableColumn<OrderItem, Double> colItemPrice;
    @FXML private TableColumn<OrderItem, Double> colItemTotal;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTax;
    @FXML private Label lblServiceCharge;
    @FXML private Label lblGrandTotal;

    @FXML private TableView<Order> ordersHistoryTable;
    @FXML private TableColumn<Order, String> colHistNumber;
    @FXML private TableColumn<Order, String> colHistTable;
    @FXML private TableColumn<Order, String> colHistCustomer;
    @FXML private TableColumn<Order, OrderStatus> colHistStatus;
    @FXML private TableColumn<Order, Double> colHistTotal;

    private final OrderService orderService = new OrderService();
    private final TableService tableService = new TableService();
    private final CustomerService customerService = new CustomerService();
    private final MenuService menuService = new MenuService();

    private final ObservableList<OrderItem> currentItemsList = FXCollections.observableArrayList();
    private final ObservableList<Order> orderHistoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        User currentUser = AuthenticationService.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Access denied", "You must log in to access the order screen.");
            return;
        }

        try {
            AuthenticationService.requireAnyRole(com.restaurant.enums.UserRole.ADMIN, com.restaurant.enums.UserRole.MANAGER,
                    com.restaurant.enums.UserRole.WAITER);
        } catch (SecurityException ex) {
            AlertUtil.showError("Access denied", ex.getMessage());
            if (tableComboBox != null) tableComboBox.setDisable(true);
            if (customerComboBox != null) customerComboBox.setDisable(true);
            if (menuItemComboBox != null) menuItemComboBox.setDisable(true);
            if (currentOrderItemsTable != null) currentOrderItemsTable.setDisable(true);
            if (ordersHistoryTable != null) ordersHistoryTable.setDisable(true);
            return;
        }

        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colItemPrice.setCellFactory(CurrencyUtil.forTableColumn());
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colItemTotal.setCellFactory(CurrencyUtil.forTableColumn());
        currentOrderItemsTable.setItems(currentItemsList);

        colHistNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colHistTable.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colHistCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colHistTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colHistTotal.setCellFactory(CurrencyUtil.forTableColumn());

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));

        loadDropdowns();
        loadOrderHistory();
        recalculateCurrentOrderTotals();
    }

    private void loadDropdowns() {
        tableComboBox.setItems(FXCollections.observableArrayList(tableService.getAllTables()));
        customerComboBox.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        menuItemComboBox.setItems(FXCollections.observableArrayList(menuService.getAllMenuItems()));
    }

    private void loadOrderHistory() {
        orderHistoryList.clear();
        orderHistoryList.addAll(orderService.getAllOrders());
        ordersHistoryTable.setItems(orderHistoryList);
    }

    @FXML
    public void handleAddItemToOrder() {
        MenuItem selectedItem = menuItemComboBox.getValue();
        if (selectedItem == null) {
            AlertUtil.showWarning("Item Required", "Please select a menu item to add.");
            return;
        }

        int qty = quantitySpinner.getValue();
        String instructions = itemInstructionsField.getText();

        // Check if item already in order list
        boolean merged = false;
        for (OrderItem oi : currentItemsList) {
            if (oi.getItemId() == selectedItem.getItemId()) {
                oi.updateQuantity(oi.getQuantity() + qty);
                merged = true;
                break;
            }
        }

        if (!merged) {
            OrderItem oi = new OrderItem(selectedItem.getItemId(), selectedItem.getName(), qty, selectedItem.getPrice(), instructions);
            currentItemsList.add(oi);
        }

        currentOrderItemsTable.refresh();
        itemInstructionsField.clear();
        quantitySpinner.getValueFactory().setValue(1);
        recalculateCurrentOrderTotals();
    }

    @FXML
    public void handleRemoveSelectedItem() {
        OrderItem selected = currentOrderItemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentItemsList.remove(selected);
            recalculateCurrentOrderTotals();
        }
    }

    private void recalculateCurrentOrderTotals() {
        double subtotal = currentItemsList.stream().mapToDouble(OrderItem::getSubtotal).sum();
        double discount = 0.0;
        double taxable = subtotal - discount;
        double tax = taxable * 0.05;
        double serviceCharge = subtotal * 0.025;
        double total = taxable + tax + serviceCharge;

        lblSubtotal.setText(CurrencyUtil.format(subtotal));
        lblDiscount.setText(CurrencyUtil.format(discount));
        lblTax.setText(CurrencyUtil.format(tax));
        lblServiceCharge.setText(CurrencyUtil.format(serviceCharge));
        lblGrandTotal.setText(CurrencyUtil.format(total));
    }

    @FXML
    public void handleCreateAndSendToKitchen() {
        if (currentItemsList.isEmpty()) {
            AlertUtil.showWarning("Empty Order", "Please add at least one menu item to the order.");
            return;
        }

        RestaurantTable table = tableComboBox.getValue();
        Customer customer = customerComboBox.getValue();
        User currentUser = AuthenticationService.getCurrentUser();
        int empId = currentUser != null ? currentUser.getUserId() : 1;

        Integer tableId = table != null ? table.getTableId() : null;
        Integer customerId = customer != null ? customer.getCustomerId() : null;

        try {
            Order order = orderService.createNewOrder(
                    customerId,
                    tableId,
                    empId,
                    new ArrayList<>(currentItemsList),
                    orderNotesArea.getText()
            );

            StringBuilder msg = new StringBuilder();
            msg.append("Order #").append(order.getOrderNumber()).append(" created and dispatched to Kitchen!\n\n");
            if (order.getInventoryDeductions() != null && !order.getInventoryDeductions().isEmpty()) {
                msg.append("Live Inventory Deductions Applied in Backend:\n");
                for (com.restaurant.model.InventoryDeductionDetail d : order.getInventoryDeductions()) {
                    msg.append(String.format("  • %s: -%.2f %s (Remaining: %.2f %s)\n",
                            d.getIngredientName(), d.getDeductedQuantity(), d.getUnit(),
                            d.getRemainingStock(), d.getUnit()));
                }
            } else {
                msg.append("(No specific raw inventory ingredients mapped for these dishes.)\n");
            }

            AlertUtil.showInfo("Order Placed & Stock Reduced", msg.toString());
            currentItemsList.clear();
            orderNotesArea.clear();
            recalculateCurrentOrderTotals();
            loadOrderHistory();
            loadDropdowns();
        } catch (Exception e) {
            AlertUtil.showError("Order Failed", e.getMessage());
        }
    }

    @FXML
    public void handleCancelOrder() {
        Order selected = ordersHistoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an order from the history to cancel.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Cancel Order", "Are you sure you want to cancel order " + selected.getOrderNumber() + "?");
        if (confirm) {
            orderService.cancelOrder(selected.getOrderId());
            loadOrderHistory();
            loadDropdowns();
        }
    }
}
