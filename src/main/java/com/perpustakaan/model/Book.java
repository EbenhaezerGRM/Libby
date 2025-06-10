package com.perpustakaan.model;

public class Book {
    private final String id;
    private final String title;
    private final String author;
    private final int publicationYear;
    private final String description;
    private final String category;
    private final int totalStock;
    private int availableStock;
    private double averageRating;

    public Book(String id, String title, String author, int publicationYear, String description, String category, int totalStock, int availableStock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.description = description;
        this.category = category;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
    }

    public boolean isAvailable() {
        return availableStock > 0;
    }

    @Override
    public String toString() {
        String availability = isAvailable() ? "Tersedia" : "Stok Habis";
        return String.format("ID: %s | Judul: %s | Penulis: %s (%d) | Stok: %d/%d (%s) | Rating: %.1f ⭐",
                id, title, author, publicationYear, availableStock, totalStock, availability, averageRating);
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublicationYear() { return publicationYear; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getTotalStock() { return totalStock; }
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public double getAverageRating() {
        return averageRating;
    }
}