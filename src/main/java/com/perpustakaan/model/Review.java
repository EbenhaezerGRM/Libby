package com.perpustakaan.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private final int rating;
    private final String comment;
    private final String username;
    private final LocalDateTime reviewDate;

    public Review(int rating, String comment, String username, LocalDateTime reviewDate) {
        this.rating = rating;
        this.comment = comment;
        this.username = username;
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("Rating: %d⭐ | Oleh: %s (%s)\n   Komentar: %s",
                rating, username, reviewDate.format(formatter), comment != null ? comment : "-");
    }
}