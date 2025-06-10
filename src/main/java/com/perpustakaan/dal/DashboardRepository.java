package com.perpustakaan.dal;

import com.perpustakaan.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

public class DashboardRepository {
    private final DatabaseConnector connector;
    public DashboardRepository(DatabaseConnector connector) { this.connector = connector; }
    
    public List<Map.Entry<String, Integer>> getMostPopularBooks(int limit) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        String sql = "SELECT b.title, COUNT(t.book_id) as borrow_count FROM transactions t JOIN books b ON t.book_id = b.id GROUP BY t.book_id ORDER BY borrow_count DESC LIMIT ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    list.add(new SimpleEntry<>(rs.getString("title"), rs.getInt("borrow_count")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Map.Entry<User, Integer>> getMostActiveUsers(int limit) {
        List<Map.Entry<User, Integer>> list = new ArrayList<>();
        String sql = "SELECT u.*, COUNT(t.user_id) as transaction_count FROM transactions t JOIN users u ON t.user_id = u.id GROUP BY t.user_id ORDER BY transaction_count DESC LIMIT ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("role"), rs.getString("status"), rs.getDouble("total_fines_unpaid"));
                    list.add(new SimpleEntry<>(user, rs.getInt("transaction_count")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}