package com.perpustakaan.dal;

import com.perpustakaan.model.Review;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    private final DatabaseConnector connector;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ReviewRepository(DatabaseConnector connector) { this.connector = connector; }

    public boolean save(String bookId, int userId, int rating, String comment) {
        String sql = "INSERT INTO reviews(book_id, user_id, rating, comment, review_date) VALUES(?,?,?,?,?) " +
                     "ON CONFLICT(book_id, user_id) DO UPDATE SET rating=excluded.rating, comment=excluded.comment, review_date=excluded.review_date";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comment);
            pstmt.setString(5, LocalDateTime.now().format(dtf));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Review> findByBookId(String bookId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.book_id = ? ORDER BY r.review_date DESC";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new Review(
                            rs.getInt("rating"), rs.getString("comment"),
                            rs.getString("username"), LocalDateTime.parse(rs.getString("review_date"), dtf)));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return reviews;
    }
}