package com.restaurant.model;

import com.restaurant.enums.TableStatus;

public class RestaurantTable {
    private int tableId;
    private String tableNumber;
    private int capacity;
    private String location;
    private TableStatus status;

    public RestaurantTable() {
        this.status = TableStatus.AVAILABLE;
        this.location = "Main Dining Hall";
    }

    public RestaurantTable(int tableId, String tableNumber, int capacity, String location, TableStatus status) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
        this.status = status != null ? status : TableStatus.AVAILABLE;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return tableNumber + " (" + capacity + " Seats - " + status.getLabel() + ")";
    }
}
