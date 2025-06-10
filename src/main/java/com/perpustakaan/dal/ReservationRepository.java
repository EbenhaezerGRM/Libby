package com.perpustakaan.dal;

import com.perpustakaan.model.Reservation;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private final DatabaseConnector connector;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ReservationRepository(DatabaseConnector connector) {
        this.connector = connector;
    }

    public boolean save(String bookId, int userId) {
        String sql = "INSERT INTO reservations(book_id, user_id, reservation_date, status) VALUES(?,?,?, 'ACTIVE')";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, LocalDateTime.now().format(dtf));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Reservation> findRelevantByUserId(int userId) {
        String sql = "SELECT r.*, b.title as book_title, u.username FROM reservations r JOIN books b ON r.book_id = b.id JOIN users u ON r.user_id = u.id WHERE r.user_id = ? AND (r.status = 'ACTIVE' OR r.status = 'READY_FOR_PICKUP') ORDER BY r.reservation_date ASC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRowToReservation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public Reservation findActiveByUserAndBook(int userId, String bookId) {
        String sql = "SELECT r.*, b.title as book_title, u.username FROM reservations r JOIN books b ON r.book_id = b.id JOIN users u ON r.user_id = u.id WHERE r.user_id = ? AND r.book_id = ? AND r.status = 'ACTIVE' LIMIT 1";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Reservation findReadyForPickupByUserAndBook(int userId, String bookId) {
        String sql = "SELECT r.*, b.title as book_title, u.username FROM reservations r JOIN books b ON r.book_id = b.id JOIN users u ON r.user_id = u.id WHERE r.user_id = ? AND r.book_id = ? AND r.status = 'READY_FOR_PICKUP' LIMIT 1";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reservation> findReadyForPickupByUserId(int userId) {
        String sql = "SELECT r.*, b.title as book_title, u.username FROM reservations r JOIN books b ON r.book_id = b.id JOIN users u ON r.user_id = u.id WHERE r.user_id = ? AND r.status = 'READY_FOR_PICKUP'";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRowToReservation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public Reservation findNextInQueue(String bookId) {
        String sql = "SELECT r.*, b.title as book_title, u.username FROM reservations r JOIN books b ON r.book_id = b.id JOIN users u ON r.user_id = u.id WHERE r.book_id = ? AND r.status = 'ACTIVE' ORDER BY r.reservation_date ASC LIMIT 1";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.toUpperCase());
            pstmt.setInt(2, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        return new Reservation(rs.getInt("id"), rs.getString("book_id"), rs.getString("book_title"),
                rs.getInt("user_id"), rs.getString("username"),
                LocalDateTime.parse(rs.getString("reservation_date"), dtf), rs.getString("status"));
    }
}