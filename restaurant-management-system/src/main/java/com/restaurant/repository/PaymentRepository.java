package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.PaymentStatus;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public boolean recordPayment(Payment payment) {
        String sql = "INSERT INTO payments (order_id, invoice_number, amount, payment_method, payment_date_time, transaction_reference, status, processed_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setString(2, payment.getInvoiceNumber());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getPaymentMethod().name());
            stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDateTime()));
            stmt.setString(6, payment.getTransactionReference());
            stmt.setString(7, payment.getStatus().name());
            stmt.setInt(8, payment.getProcessedBy());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setPaymentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error recording payment transaction: " + payment.getInvoiceNumber(), e);
        }
        return false;
    }

    public Payment findByOrderId(int orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding payment for order ID: " + orderId, e);
        }
        return null;
    }

    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date_time DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching payments", e);
        }
        return list;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        PaymentMethod method = PaymentMethod.valueOf(rs.getString("payment_method"));
        Payment payment;
        switch (method) {
            case CASH -> payment = new CashPayment();
            case UPI -> payment = new UPIPayment();
            case CREDIT_CARD, DEBIT_CARD -> payment = new CardPayment();
            default -> payment = new CashPayment();
        }
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setInvoiceNumber(rs.getString("invoice_number"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(method);
        payment.setPaymentDateTime(rs.getTimestamp("payment_date_time").toLocalDateTime());
        payment.setTransactionReference(rs.getString("transaction_reference"));
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        payment.setProcessedBy(rs.getInt("processed_by"));
        return payment;
    }
}
