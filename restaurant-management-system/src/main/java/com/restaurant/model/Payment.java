package com.restaurant.model;

import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.PaymentStatus;
import com.restaurant.exception.PaymentFailedException;
import com.restaurant.util.CurrencyUtil;
import com.restaurant.util.DateTimeUtil;

import java.time.LocalDateTime;

/**
 * Abstract base class demonstrating Polymorphism for Payment Processing.
 */
public abstract class Payment {
    protected int paymentId;
    protected int orderId;
    protected String invoiceNumber;
    protected double amount;
    protected PaymentMethod paymentMethod;
    protected LocalDateTime paymentDateTime;
    protected String transactionReference;
    protected PaymentStatus status;
    protected int processedBy;

    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.paymentDateTime = LocalDateTime.now();
    }

    public Payment(int orderId, String invoiceNumber, double amount, PaymentMethod paymentMethod, int processedBy) {
        this.orderId = orderId;
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.processedBy = processedBy;
        this.status = PaymentStatus.PENDING;
        this.paymentDateTime = LocalDateTime.now();
    }

    /**
     * Polymorphic payment processing method.
     * Subclasses validate specific payment channel constraints.
     */
    public abstract boolean processPayment() throws PaymentFailedException;

    /**
     * Polymorphic receipt string generator tailored to payment mode.
     */
    public abstract String generateReceipt(Order order);

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(LocalDateTime paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public int getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(int processedBy) {
        this.processedBy = processedBy;
    }
}
