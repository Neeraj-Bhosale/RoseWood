package com.restaurant.controller;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.service.KitchenService;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class KitchenController {

    @FXML private TableView<Order> kitchenOrdersTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colTable;
    @FXML private TableColumn<Order, String> colOrderNumber;
    @FXML private TableColumn<Order, OrderStatus> colStatus;
    @FXML private TableColumn<Order, String> colTime;

    @FXML private ListView<String> orderItemsListView;
    @FXML private TextArea notesArea;

    @FXML private Button btnStartPreparing;
    @FXML private Button btnMarkReady;
    @FXML private Button btnMarkServed;

    private final KitchenService kitchenService = new KitchenService();
    private final ObservableList<Order> kitchenList = FXCollections.observableArrayList();
    private Order selectedOrder;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));

        kitchenOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedOrder = newVal;
                displayOrderDetails(newVal);
            }
        });

        loadKitchenOrders();
    }

    private void displayOrderDetails(Order order) {
        orderItemsListView.getItems().clear();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String desc = item.getQuantity() + "x  " + item.getItemName();
                if (item.getSpecialInstructions() != null && !item.getSpecialInstructions().isEmpty()) {
                    desc += " [Note: " + item.getSpecialInstructions() + "]";
                }
                orderItemsListView.getItems().add(desc);
            }
        }
        notesArea.setText(order.getSpecialNotes() != null ? order.getSpecialNotes() : "No special instructions.");
    }

    @FXML
    public void loadKitchenOrders() {
        kitchenList.clear();
        kitchenList.addAll(kitchenService.getActiveKitchenOrders());
        kitchenOrdersTable.setItems(kitchenList);
        orderItemsListView.getItems().clear();
        notesArea.clear();
        selectedOrder = null;
    }

    @FXML
    public void handleStartPreparing() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order from the kitchen queue.");
            return;
        }
        kitchenService.markAsPreparing(selectedOrder.getOrderId());
        loadKitchenOrders();
    }

    @FXML
    public void handleMarkReady() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order.");
            return;
        }
        kitchenService.markAsReady(selectedOrder.getOrderId());
        loadKitchenOrders();
    }

    @FXML
    public void handleMarkServed() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order.");
            return;
        }
        kitchenService.markAsServed(selectedOrder.getOrderId());
        loadKitchenOrders();
    }
}
