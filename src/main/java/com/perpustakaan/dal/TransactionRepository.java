package com.perpustakaan.dal;

import com.perpustakaan.model.Transaction;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private final DatabaseConnector connector;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TransactionRepository(DatabaseConnector connector) { this.connector = connector; }
    
    public boolean save(String bookId, int userId, LocalDateTime dueDate) {
        String sql = "INSERT INTO transactions(book_id, user_id, borrow_time, due_date) VALUES(?,?,?,?)";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, LocalDateTime.now().format(dtf));
            pstmt.setString(4, dueDate.format(dtf));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean updateReturn(int transactionId, double fineAmount) {
        String sql = "UPDATE transactions SET return_time = ?, fine_amount = ? WHERE id = ?";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().format(dtf));
            pstmt.setDouble(2, fineAmount);
            pstmt.setInt(3, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Transaction> findAll() {
        String sql = "SELECT t.*, b.title as book_title, u.username as borrower_name FROM transactions t JOIN books b ON t.book_id = b.id JOIN users u ON t.user_id = u.id ORDER BY t.borrow_time DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = connector.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) { transactions.add(mapRowToTransaction(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return transactions;
    }
    
    public List<Transaction> findByUserId(int userId) {
        String sql = "SELECT t.*, b.title as book_title, u.username as borrower_name FROM transactions t JOIN books b ON t.book_id = b.id JOIN users u ON t.user_id = u.id WHERE t.user_id = ? ORDER BY t.borrow_time DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try(ResultSet rs = pstmt.executeQuery()){
                 while(rs.next()) { transactions.add(mapRowToTransaction(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return transactions;
    }
    
    public List<Transaction> findOpenByUserId(int userId) {
        String sql = "SELECT t.*, b.title as book_title, u.username as borrower_name " +
                     "FROM transactions t JOIN books b ON t.book_id = b.id JOIN users u ON t.user_id = u.id " +
                     "WHERE t.user_id = ? AND t.return_time IS NULL ORDER BY t.borrow_time DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try(ResultSet rs = pstmt.executeQuery()){
                 while(rs.next()) { transactions.add(mapRowToTransaction(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return transactions;
    }
    
    public Transaction findOpenTransactionByBookAndUser(String bookId, int userId) {
        String sql = "SELECT t.*, b.title as book_title, u.username as borrower_name FROM transactions t JOIN books b ON t.book_id = b.id JOIN users u ON t.user_id = u.id WHERE t.book_id = ? AND t.user_id = ? AND t.return_time IS NULL LIMIT 1";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapRowToTransaction(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int countOpenTransactionsByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND return_time IS NULL";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int countOpenTransactionsForBook(String bookId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE book_id = ? AND return_time IS NULL";
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Transaction> findOverdueBooks() {
        String sql = "SELECT t.*, b.title as book_title, u.username as borrower_name " +
                     "FROM transactions t JOIN books b ON t.book_id = b.id JOIN users u ON t.user_id = u.id " +
                     "WHERE t.return_time IS NULL AND t.due_date < ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = connector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().format(dtf));
             try(ResultSet rs = pstmt.executeQuery()){
                 while(rs.next()) { transactions.add(mapRowToTransaction(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return transactions;
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        String returnTimeStr = rs.getString("return_time");
        return new Transaction(
                rs.getInt("id"), rs.getString("book_id"), rs.getInt("user_id"),
                rs.getString("book_title"), rs.getString("borrower_name"),
                LocalDateTime.parse(rs.getString("borrow_time"), dtf),
                (returnTimeStr == null) ? null : LocalDateTime.parse(returnTimeStr, dtf),
                LocalDateTime.parse(rs.getString("due_date"), dtf),
                rs.getDouble("fine_amount"));
    }
}