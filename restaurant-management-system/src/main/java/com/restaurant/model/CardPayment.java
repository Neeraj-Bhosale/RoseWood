package com.restaurant.model;

import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.PaymentStatus;
import com.restaurant.exception.PaymentFailedException;
import com.restaurant.util.CurrencyUtil;
import com.restaurant.util.DateTimeUtil;

public class CardPayment extends Payment {
    private String cardNumberLastFour;
    private String approvalCode;

    public CardPayment() {
        this.paymentMethod = PaymentMethod.CREDIT_CARD;
    }

    public CardPayment(int orderId, String invoiceNumber, double amount, PaymentMethod method, int processedBy, String cardNumberLastFour, String approvalCode) {
        super(orderId, invoiceNumber, amount, method, processedBy);
        this.cardNumberLastFour = cardNumberLastFour;
        this.approvalCode = approvalCode;
        this.transactionReference = "CARD-AUTH-" + approvalCode;
    }

    @Override
    public boolean processPayment() throws PaymentFailedException {
        if (cardNumberLastFour == null || cardNumberLastFour.length() != 4) {
            this.status = PaymentStatus.FAILED;
            throw new PaymentFailedException("Invalid card digits. Last 4 digits required.");
        }
        if (approvalCode == null || approvalCode.trim().isEmpty()) {
            this.status = PaymentStatus.FAILED;
            throw new PaymentFailedException("Card authorization approval code missing from POS terminal.");
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
        sb.append(String.format("Pay Mode   : %s (**** %s)\n", paymentMethod.getDisplayName(), cardNumberLastFour));
        sb.append(String.format("Auth Code  : %s\n", approvalCode));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Total Paid : %s [APPROVED]\n", CurrencyUtil.format(amount)));
        sb.append("-----------------------------------------\n");
        sb.append("       THANK YOU FOR DINING WITH US!     \n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    public String getCardNumberLastFour() {
        return cardNumberLastFour;
    }

    public void setCardNumberLastFour(String cardNumberLastFour) {
        this.cardNumberLastFour = cardNumberLastFour;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }
}
