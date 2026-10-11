package com.restaurant.util;

import java.util.regex.Pattern;

/**
 * Utility for input validation and sanitization.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s-]{7,15}$");

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isPositive(double value) {
        return value > 0;
    }

    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    public static boolean isValidPrice(double price) {
        return price >= 0;
    }

    public static boolean isValidCapacity(int capacity) {
        return capacity > 0 && capacity <= 100;
    }

    public static boolean isValidDiscount(double discount, double subtotal) {
        return discount >= 0 && discount <= subtotal;
    }
}
