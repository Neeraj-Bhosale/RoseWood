package com.restaurant.exception;

public class TableNotAvailableException extends Exception {
    public TableNotAvailableException(String message) {
        super(message);
    }
}
