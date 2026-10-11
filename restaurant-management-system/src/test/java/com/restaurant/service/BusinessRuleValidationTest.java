package com.restaurant.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurant.enums.UserRole;
import com.restaurant.exception.InsufficientStockException;
import com.restaurant.exception.InvalidReservationException;
import com.restaurant.model.InventoryItem;
import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.InventoryRepository;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.repository.TableRepository;

public class BusinessRuleValidationTest {

    @Test
    @DisplayName("Access control should block unauthorized roles")
    public void shouldRejectUnauthorizedRole() {
        assertThrows(SecurityException.class, () -> AuthenticationService.requireAnyRole(UserRole.ADMIN));
    }

    @Test
    @DisplayName("Stock deduction should reject insufficient inventory")
    public void shouldRejectInsufficientInventoryForOrder() {
        InventoryService inventoryService = new InventoryService(new InventoryRepository() {
            @Override
            public List<InventoryItem> findAll() {
                return List.of(new InventoryItem(1, "Chicken", "Meat", 5.0, "kg", 2.0, 200.0, 1));
            }
        });

        Map<Integer, Double> deductions = new HashMap<>();
        deductions.put(1, 6.0);

        assertThrows(InsufficientStockException.class, () -> inventoryService.deductStockForOrder(deductions));
    }

    @Test
    @DisplayName("Reservation service should block double-booked tables")
    public void shouldRejectBookingConflict() {
        ReservationService service = new ReservationService(
                new ReservationRepository() {
                    @Override
                    public boolean hasConflict(int tableId, LocalDate date, LocalTime time, int excludeReservationId) {
                        return true;
                    }
                },
                new TableRepository() {
                    @Override
                    public RestaurantTable findById(int tableId) {
                        return new RestaurantTable(1, "T1", 4, "Main Hall", com.restaurant.enums.TableStatus.AVAILABLE);
                    }
                }
        );

        assertThrows(InvalidReservationException.class,
                () -> service.createReservation(1, 1, LocalDate.now().plusDays(1), LocalTime.of(18, 30), 3, "Window table"));
    }
}
