package com.restaurant.model;

import com.restaurant.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private int reservationId;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private int tableId;
    private String tableNumber;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private int guestCount;
    private String specialRequest;
    private ReservationStatus status;

    public Reservation() {
        this.status = ReservationStatus.PENDING;
        this.reservationDate = LocalDate.now();
        this.reservationTime = LocalTime.of(19, 0);
        this.guestCount = 2;
    }

    public Reservation(int customerId, int tableId, LocalDate reservationDate, LocalTime reservationTime, int guestCount) {
        this();
        this.customerId = customerId;
        this.tableId = tableId;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.guestCount = guestCount;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
