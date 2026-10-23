package com.rosewood.repository;

import com.rosewood.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByStatusOrderByOrderDateTimeDesc(String status);
    List<Order> findAllByOrderByOrderDateTimeDesc();

    @Query("SELECT o FROM Order o WHERE o.orderDateTime BETWEEN :start AND :end ORDER BY o.orderDateTime DESC")
    List<Order> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status != 'CANCELLED'")
    long countActiveOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED'")
    java.math.BigDecimal sumTotalRevenue();
}
