package com.restaurant.controller;

import com.restaurant.enums.ReservationStatus;
import com.restaurant.model.Customer;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import com.restaurant.service.CustomerService;
import com.restaurant.service.ReservationService;
import com.restaurant.service.TableService;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> colId;
    @FXML private TableColumn<Reservation, String> colCustomer;
    @FXML private TableColumn<Reservation, String> colPhone;
    @FXML private TableColumn<Reservation, String> colTable;
    @FXML private TableColumn<Reservation, LocalDate> colDate;
    @FXML private TableColumn<Reservation, LocalTime> colTime;
    @FXML private TableColumn<Reservation, Integer> colGuests;
    @FXML private TableColumn<Reservation, ReservationStatus> colStatus;

    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<RestaurantTable> tableCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Spinner<Integer> guestsSpinner;
    @FXML private TextArea requestArea;

    private final ReservationService reservationService = new ReservationService();
    private final CustomerService customerService = new CustomerService();
    private final TableService tableService = new TableService();
    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));
        colGuests.setCellValueFactory(new PropertyValueFactory<>("guestCount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        datePicker.setValue(LocalDate.now());
        timeField.setText("19:30");
        guestsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));

        loadDropdowns();
        loadReservations();
    }

    private void loadDropdowns() {
        customerCombo.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        tableCombo.setItems(FXCollections.observableArrayList(tableService.getAllTables()));
    }

    private void loadReservations() {
        reservationList.clear();
        reservationList.addAll(reservationService.getAllReservations());
        reservationTable.setItems(reservationList);
    }

    @FXML
    public void handleCreateReservation() {
        Customer cust = customerCombo.getValue();
        RestaurantTable table = tableCombo.getValue();
        LocalDate date = datePicker.getValue();
        String timeStr = timeField.getText().trim();
        int guests = guestsSpinner.getValue();

        if (cust == null || table == null || date == null || timeStr.isEmpty()) {
            AlertUtil.showWarning("Missing Fields", "Please select customer, table, date and time.");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeStr.length() == 5 ? timeStr + ":00" : timeStr);
            boolean created = reservationService.createReservation(
                    cust.getCustomerId(),
                    table.getTableId(),
                    date,
                    time,
                    guests,
                    requestArea.getText()
            );

            if (created) {
                AlertUtil.showInfo("Reserved", "Table #" + table.getTableNumber() + " reserved for " + cust.getName());
                loadReservations();
                requestArea.clear();
            }
        } catch (Exception e) {
            AlertUtil.showError("Reservation Error", e.getMessage());
        }
    }

    @FXML
    public void handleCancelReservation() {
        Reservation r = reservationTable.getSelectionModel().getSelectedItem();
        if (r == null) {
            AlertUtil.showWarning("No Selection", "Please select a reservation to cancel.");
            return;
        }
        reservationService.updateStatus(r.getReservationId(), ReservationStatus.CANCELLED);
        loadReservations();
    }

    @FXML
    public void handleCompleteReservation() {
        Reservation r = reservationTable.getSelectionModel().getSelectedItem();
        if (r == null) {
            AlertUtil.showWarning("No Selection", "Please select a reservation to complete.");
            return;
        }
        reservationService.updateStatus(r.getReservationId(), ReservationStatus.COMPLETED);
        loadReservations();
    }
}
