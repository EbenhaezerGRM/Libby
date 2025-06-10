package com.perpustakaan.dal;

import com.perpustakaan.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final DatabaseConnector connector;
    public UserRepository(DatabaseConnector connector) { this.connector = connector; }
    
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        try (Connection conn = connector.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { users.add(mapRowToUser(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapRowToUser(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapRowToUser(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean save(String username, String hashedPassword) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?,?, 'USER')";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updatePassword(String username, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public boolean updateUserStatus(String username, String status) {
        String sql = "UPDATE users SET status = ? WHERE username = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.toUpperCase());
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public boolean updateUserFine(int userId, double newFineAmount) {
        String sql = "UPDATE users SET total_fines_unpaid = ? WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newFineAmount);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("role"), rs.getString("status"), rs.getDouble("total_fines_unpaid"));
    }
}