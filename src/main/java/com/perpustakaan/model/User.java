package com.perpustakaan.model;

public class User {
    private final int id;
    private final String username;
    private final String hashedPassword;
    private final String role;
    private String status; 
    private double totalFinesUnpaid;

    public User(int id, String username, String hashedPassword, String role, String status, double totalFinesUnpaid) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.status = status;
        this.totalFinesUnpaid = totalFinesUnpaid;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Username: %s | Role: %s | Status: %s | Denda Belum Dibayar: Rp %,.0f",
                id, username, role, status, totalFinesUnpaid);
    }
    
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public double getTotalFinesUnpaid() { return totalFinesUnpaid; }
}