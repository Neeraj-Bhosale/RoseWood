package com.restaurant.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for date and time formatting and calculations.
 */
public class DateTimeUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    public static final DateTimeFormatter SQL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter SQL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String formatDate(LocalDate date) {
        if (date == null) return "-";
        return date.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        if (time == null) return "-";
        return time.format(TIME_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DATETIME_FORMATTER);
    }

    public static String getReceiptTimestamp() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
}
