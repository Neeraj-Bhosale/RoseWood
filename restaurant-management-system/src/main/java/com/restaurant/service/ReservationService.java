package com.restaurant.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.restaurant.enums.ReservationStatus;
import com.restaurant.enums.TableStatus;
import com.restaurant.exception.InvalidReservationException;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.repository.TableRepository;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.tableRepository = new TableRepository();
    }

    public ReservationService(ReservationRepository reservationRepository, TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void validateReservationRequest(int tableId, LocalDate date, LocalTime time, int guestCount) throws InvalidReservationException {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Reservation date cannot be in the past.");
        }
        if (time == null) {
            throw new InvalidReservationException("Reservation time is required.");
        }
        if (guestCount <= 0) {
            throw new InvalidReservationException("Guest count must be at least 1.");
        }

        RestaurantTable table = tableRepository.findById(tableId);
        if (table == null) {
            throw new InvalidReservationException("Selected table not found.");
        }
        if (guestCount > table.getCapacity()) {
            throw new InvalidReservationException("Guest count exceeds table capacity (" + table.getCapacity() + ").");
        }
        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new InvalidReservationException("Table " + table.getTableNumber() + " is currently occupied and cannot be reserved.");
        }
    }

    public boolean createReservation(int customerId, int tableId, LocalDate date, LocalTime time, int guestCount, String specialRequest)
            throws InvalidReservationException {

        validateReservationRequest(tableId, date, time, guestCount);

        RestaurantTable table = tableRepository.findById(tableId);
        if (reservationRepository.hasConflict(tableId, date, time, 0)) {
            throw new InvalidReservationException("Table " + table.getTableNumber() + " already has a reservation booked at that time.");
        }

        Reservation res = new Reservation(customerId, tableId, date, time, guestCount);
        res.setSpecialRequest(specialRequest);
        res.setStatus(ReservationStatus.CONFIRMED);

        boolean created = reservationRepository.create(res);
        if (created && date.equals(LocalDate.now())) {
            tableRepository.updateStatus(tableId, TableStatus.RESERVED);
        }
        return created;
    }

    public boolean updateStatus(int reservationId, ReservationStatus status) {
        return reservationRepository.updateStatus(reservationId, status);
    }
}
