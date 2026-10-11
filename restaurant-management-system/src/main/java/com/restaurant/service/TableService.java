package com.restaurant.service;

import com.restaurant.enums.TableStatus;
import com.restaurant.exception.TableNotAvailableException;
import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.TableRepository;
import com.restaurant.util.ValidationUtil;

import java.util.List;

public class TableService {
    private final TableRepository tableRepository;

    public TableService() {
        this.tableRepository = new TableRepository();
    }

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestaurantTable getTableById(int tableId) {
        return tableRepository.findById(tableId);
    }

    public boolean addTable(String tableNumber, int capacity, String location) {
        if (!ValidationUtil.isNotEmpty(tableNumber)) {
            throw new IllegalArgumentException("Table number cannot be empty.");
        }
        if (!ValidationUtil.isValidCapacity(capacity)) {
            throw new IllegalArgumentException("Table capacity must be between 1 and 100.");
        }
        RestaurantTable table = new RestaurantTable(0, tableNumber.trim(), capacity, location, TableStatus.AVAILABLE);
        return tableRepository.create(table);
    }

    public boolean updateTable(RestaurantTable table) {
        if (table == null || table.getTableId() <= 0) return false;
        return tableRepository.update(table);
    }

    public boolean changeTableStatus(int tableId, TableStatus status) {
        return tableRepository.updateStatus(tableId, status);
    }

    public void verifyTableAvailability(int tableId) throws TableNotAvailableException {
        RestaurantTable table = tableRepository.findById(tableId);
        if (table == null) {
            throw new TableNotAvailableException("Table #" + tableId + " does not exist.");
        }
        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new TableNotAvailableException("Table " + table.getTableNumber() + " is currently OCCUPIED.");
        }
    }

    public boolean deleteTable(int tableId) {
        return tableRepository.delete(tableId);
    }
}
