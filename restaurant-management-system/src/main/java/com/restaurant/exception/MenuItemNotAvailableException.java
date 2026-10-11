package com.restaurant.exception;

public class MenuItemNotAvailableException extends Exception {
    public MenuItemNotAvailableException(String message) {
        super(message);
    }
}
