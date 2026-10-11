package com.restaurant.model;

import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.PaymentStatus;
import com.restaurant.exception.PaymentFailedException;
import com.restaurant.util.CurrencyUtil;
import com.restaurant.util.DateTimeUtil;

public class UPIPayment extends Payment {
    private String upiId;

    public UPIPayment() {
        this.paymentMethod = PaymentMethod.UPI;
    }

    public UPIPayment(int orderId, String invoiceNumber, double amount, int processedBy, String upiId, String transactionReference) {
        super(orderId, invoiceNumber, amount, PaymentMethod.UPI, processedBy);
        this.upiId = upiId;
        this.transactionReference = (transactionReference != null && !transactionReference.isEmpty()) 
                ? transactionReference : "UPI-" + System.currentTimeMillis();
    }

    @Override
    public boolean processPayment() throws PaymentFailedException {
        if (upiId == null || !upiId.contains("@")) {
            this.status = PaymentStatus.FAILED;
            throw new PaymentFailedException("Invalid UPI Virtual Payment Address (VPA): " + upiId);
        }
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
        sb.append(String.format("UPI ID     : %s\n", upiId));
        sb.append(String.format("Txn Ref    : %s\n", transactionReference));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Total Paid : %s [SUCCESS]\n", CurrencyUtil.format(amount)));
        sb.append("-----------------------------------------\n");
        sb.append("       THANK YOU FOR DINING WITH US!     \n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
