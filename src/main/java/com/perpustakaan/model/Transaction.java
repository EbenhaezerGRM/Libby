package com.perpustakaan.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final int id;
    private final String bookId;
    private final int userId;
    private final String bookTitle;
    private final String borrowerName;
    private final LocalDateTime borrowTime;
    private LocalDateTime returnTime;
    private final LocalDateTime dueDate;
    private double fineAmount;

    public Transaction(int id, String bookId, int userId, String bookTitle, String borrowerName, LocalDateTime borrowTime, LocalDateTime returnTime, LocalDateTime dueDate, double fineAmount) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.borrowerName = borrowerName;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.dueDate = dueDate;
        this.fineAmount = fineAmount;
    }

    public String getBookTitle() {
		return bookTitle;
	}

	@Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String status = (returnTime == null) ? "Masih Dipinjam" : "Dikembalikan pada " + returnTime.format(formatter);
        
        return String.format("ID Transaksi: %d | Buku: '%s' (ID: %s) | Peminjam: %s | Tgl Pinjam: %s | Jatuh Tempo: %s | Status: %s | Denda: Rp %,.0f",
                id, bookTitle, bookId, borrowerName, borrowTime.format(formatter), dueDate.format(formatter), status, fineAmount);
    }
    
    public int getId() { return id; }
    public String getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getBorrowTime() { return borrowTime; }
    public LocalDateTime getReturnTime() { return returnTime; }
    public double getFineAmount() { return fineAmount; }
}