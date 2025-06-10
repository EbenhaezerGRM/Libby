package com.perpustakaan.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private final int id;
    private final String bookId;
    private final String bookTitle;
    private final int userId;
    private final String username;
    private final LocalDateTime reservationDate;
    private String status; 

    public Reservation(int id, String bookId, String bookTitle, int userId, String username, LocalDateTime reservationDate, String status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.username = username;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        String statusText = status;
        if("READY_FOR_PICKUP".equals(status)) {
            statusText = "Siap Diambil!";
        } else if("ACTIVE".equals(status)){
            statusText = "Dalam Antrean";
        }
        return String.format("ID: %d | Buku: '%s' | Tgl Reservasi: %s | Status: %s",
                id, bookTitle, reservationDate.format(formatter), statusText);
    }

    public int getId() { return id; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public String getStatus() { return status; }
}