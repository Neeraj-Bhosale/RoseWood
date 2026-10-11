package com.restaurant.model;

import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.PaymentStatus;
import com.restaurant.exception.PaymentFailedException;
import com.restaurant.util.CurrencyUtil;
import com.restaurant.util.DateTimeUtil;

public class CashPayment extends Payment {
    private double cashTendered;
    private double changeDue;

    public CashPayment() {
        this.paymentMethod = PaymentMethod.CASH;
    }

    public CashPayment(int orderId, String invoiceNumber, double amount, int processedBy, double cashTendered) {
        super(orderId, invoiceNumber, amount, PaymentMethod.CASH, processedBy);
        this.cashTendered = cashTendered;
        this.changeDue = Math.max(0, cashTendered - amount);
        this.transactionReference = "CASH-TXN-" + System.currentTimeMillis() % 1000000;
    }

    @Override
    public boolean processPayment() throws PaymentFailedException {
        if (cashTendered < amount) {
            this.status = PaymentStatus.FAILED;
            throw new PaymentFailedException(String.format("Insufficient cash tendered! Required: %s, Received: %s",
                    CurrencyUtil.format(amount), CurrencyUtil.format(cashTendered)));
        }
        this.changeDue = cashTendered - amount;
        this.status = PaymentStatus.PAID;
        return true;
    }

    @Override
    public String generateReceipt(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("         ROSEWOOD RESTAURANT          \n");
        sb.append("        104 Gourmet Blvd, Silicon City   \n");
        sb.append("=========================================\n");
        sb.append(String.format("Invoice No : %s\n", invoiceNumber));
        sb.append(String.format("Order No   : %s\n", order != null ? order.getOrderNumber() : "ORD-" + orderId));
        sb.append(String.format("Date/Time  : %s\n", DateTimeUtil.formatDateTime(paymentDateTime)));
        sb.append(String.format("Pay Mode   : %s\n", paymentMethod.getDisplayName()));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Bill Amount  : %s\n", CurrencyUtil.format(amount)));
        sb.append(String.format("Cash Given   : %s\n", CurrencyUtil.format(cashTendered)));
        sb.append(String.format("Change Back  : %s\n", CurrencyUtil.format(changeDue)));
        sb.append("-----------------------------------------\n");
        sb.append("       THANK YOU FOR DINING WITH US!     \n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    public double getChangeDue() {
        return changeDue;
    }

    public void setChangeDue(double changeDue) {
        this.changeDue = changeDue;
    }
}
