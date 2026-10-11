package com.restaurant.service;

import java.util.List;
import java.util.logging.Logger;

import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.PaymentStatus;
import com.restaurant.enums.TableStatus;
import com.restaurant.exception.PaymentFailedException;
import com.restaurant.model.Customer;
import com.restaurant.model.Order;
import com.restaurant.model.Payment;
import com.restaurant.repository.CustomerRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.PaymentRepository;
import com.restaurant.repository.TableRepository;

public class PaymentService {
    private static final Logger LOGGER = Logger.getLogger(PaymentService.class.getName());
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final CustomerRepository customerRepository;

    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.orderRepository = new OrderRepository();
        this.tableRepository = new TableRepository();
        this.customerRepository = new CustomerRepository();
    }

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                          TableRepository tableRepository, CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Executes the payment polymorphically and finalizes the order workflow.
     */
    public boolean processAndFinalizePayment(Payment payment, Order order) throws PaymentFailedException {
        if (payment == null || order == null) {
            throw new PaymentFailedException("Invalid payment or order data.");
        }

        if (order.getOrderId() <= 0) {
            throw new PaymentFailedException("Order must be saved before payment can be processed.");
        }

        if (paymentRepository.findByOrderId(order.getOrderId()) != null) {
            throw new PaymentFailedException("This order has already been paid for.");
        }

        if (Math.abs(payment.getAmount() - order.getTotalAmount()) > 0.01) {
            throw new PaymentFailedException("Payment amount does not match the order total. Expected: " + order.getTotalAmount() + ", Received: " + payment.getAmount());
        }

        boolean success = payment.processPayment();
        if (!success || payment.getStatus() != PaymentStatus.PAID) {
            throw new PaymentFailedException("Payment processing could not be completed.");
        }

        paymentRepository.recordPayment(payment);
        orderRepository.updateStatus(order.getOrderId(), OrderStatus.COMPLETED);

        // Release table -> CLEANING
        if (order.getTableId() != null && order.getTableId() > 0) {
            tableRepository.updateStatus(order.getTableId(), TableStatus.CLEANING);
        }

        // Update customer spending and loyalty points
        if (order.getCustomerId() != null && order.getCustomerId() > 0) {
            Customer customer = customerRepository.findById(order.getCustomerId());
            if (customer != null) {
                customer.addSpending(payment.getAmount());
                customerRepository.update(customer);
            }
        }

        LOGGER.info("Payment finalized successfully for Order: " + order.getOrderNumber() + ", Inv: " + payment.getInvoiceNumber());
        return true;
    }

    public Payment getPaymentForOrder(int orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
