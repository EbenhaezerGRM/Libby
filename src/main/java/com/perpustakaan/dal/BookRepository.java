package com.perpustakaan.dal;

import com.perpustakaan.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private final DatabaseConnector connector;

    public BookRepository(DatabaseConnector connector) { this.connector = connector; }

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, COALESCE(AVG(r.rating), 0) as avg_rating FROM books b LEFT JOIN reviews r ON b.id = r.book_id GROUP BY b.id ORDER BY b.category, b.title";
        try (Connection conn = connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRowToBook(rs, true));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }

    public Book findById(String id) {
        String sql = "SELECT b.*, COALESCE(AVG(r.rating), 0) as avg_rating FROM books b LEFT JOIN reviews r ON b.id = r.book_id WHERE b.id = ? GROUP BY b.id";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapRowToBook(rs, true); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean save(Book book) {
        String sql = "INSERT INTO books(id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setString(5, book.getDescription());
            pstmt.setString(6, book.getCategory());
            pstmt.setInt(7, book.getTotalStock());
            pstmt.setInt(8, book.getAvailableStock());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, publicationYear = ?, description = ?, category = ?, total_stock = ?, available_stock = ? WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getPublicationYear());
            pstmt.setString(4, book.getDescription());
            pstmt.setString(5, book.getCategory());
            pstmt.setInt(6, book.getTotalStock());
            pstmt.setInt(7, book.getAvailableStock());
            pstmt.setString(8, book.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean updateStock(String bookId, int newAvailableStock) {
        String sql = "UPDATE books SET available_stock = ? WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newAvailableStock);
            pstmt.setString(2, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus buku. Pastikan tidak ada transaksi aktif atau reservasi. Error: " + e.getMessage());
            return false;
        }
    }

    private Book mapRowToBook(ResultSet rs, boolean withRating) throws SQLException {
        Book book = new Book(
                rs.getString("id"), rs.getString("title"), rs.getString("author"),
                rs.getInt("publicationYear"), rs.getString("description"), rs.getString("category"),
                rs.getInt("total_stock"), rs.getInt("available_stock"));
        if (withRating) {
            book.setAverageRating(rs.getDouble("avg_rating"));
        }
        return book;
    }
}