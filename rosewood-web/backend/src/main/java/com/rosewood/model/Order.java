package com.rosewood.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_number", unique = true, nullable = false, length = 30)
    private String orderNumber;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "CREATED";

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "service_charge", nullable = false)
    private BigDecimal serviceCharge = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;

    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    // Transient: populated from joins
    @Transient
    private String customerName;

    @Transient
    private String tableNumber;

    @Transient
    private String employeeName;

    @PrePersist
    protected void onCreate() {
        if (orderDateTime == null) {
            orderDateTime = LocalDateTime.now();
        }
    }
}
