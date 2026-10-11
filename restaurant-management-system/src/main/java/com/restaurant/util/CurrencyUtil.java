package com.restaurant.util;

import com.restaurant.config.AppConfig;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.DecimalFormat;

/**
 * Utility for formatting currency and prices consistently in Indian Rupees (₹).
 */
public class CurrencyUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    public static final String CURRENCY_SYMBOL = "\u20B9";

    public static String getSymbol() {
        String symbol = AppConfig.getInstance().getProperty("app.currency", CURRENCY_SYMBOL);
        if (symbol == null || symbol.trim().isEmpty() || symbol.contains("?") || symbol.contains("\uFFFD")) {
            return CURRENCY_SYMBOL;
        }
        return symbol;
    }

    public static String format(double amount) {
        return getSymbol() + " " + DECIMAL_FORMAT.format(amount);
    }

    public static String formatPlain(double amount) {
        return DECIMAL_FORMAT.format(amount);
    }

    /**
     * Cell factory for formatting Double TableColumn cells with the currency symbol.
     */
    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> forTableColumn() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format(item));
                }
            }
        };
    }
}

