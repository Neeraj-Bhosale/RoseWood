package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.enums.ReservationStatus;
import com.restaurant.exception.DatabaseOperationException;
import com.restaurant.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, c.name as customer_name, c.phone as customer_phone, t.table_number " +
                     "FROM reservations r " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN restaurant_tables t ON r.table_id = t.table_id " +
                     "ORDER BY r.reservation_date DESC, r.reservation_time ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching reservations", e);
        }
        return list;
    }

    public boolean hasConflict(int tableId, LocalDate date, LocalTime time, int excludeReservationId) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                     "WHERE table_id = ? AND reservation_date = ? " +
                     "AND status IN ('PENDING', 'CONFIRMED') " +
                     "AND reservation_id != ? " +
                     "AND (ABS(TIME_TO_SEC(TIMEDIFF(reservation_time, ?))) < 7200)"; // 2-hour window conflict check
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tableId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setInt(3, excludeReservationId);
            stmt.setTime(4, Time.valueOf(time));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error checking reservation conflict", e);
        }
        return false;
    }

    public boolean create(Reservation r) {
        String sql = "INSERT INTO reservations (customer_id, table_id, reservation_date, reservation_time, guest_count, special_request, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, r.getCustomerId());
            stmt.setInt(2, r.getTableId());
            stmt.setDate(3, Date.valueOf(r.getReservationDate()));
            stmt.setTime(4, Time.valueOf(r.getReservationTime()));
            stmt.setInt(5, r.getGuestCount());
            stmt.setString(6, r.getSpecialRequest());
            stmt.setString(7, r.getStatus().name());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        r.setReservationId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating reservation", e);
        }
        return false;
    }

    public boolean updateStatus(int reservationId, ReservationStatus status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating reservation status: " + reservationId, e);
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setCustomerId(rs.getInt("customer_id"));
        r.setCustomerName(rs.getString("customer_name"));
        r.setCustomerPhone(rs.getString("customer_phone"));
        r.setTableId(rs.getInt("table_id"));
        r.setTableNumber(rs.getString("table_number"));
        r.setReservationDate(rs.getDate("reservation_date").toLocalDate());
        r.setReservationTime(rs.getTime("reservation_time").toLocalTime());
        r.setGuestCount(rs.getInt("guest_count"));
        r.setSpecialRequest(rs.getString("special_request"));
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        return r;
    }
}
