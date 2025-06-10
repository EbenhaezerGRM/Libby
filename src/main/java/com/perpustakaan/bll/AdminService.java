package com.perpustakaan.bll;

import com.perpustakaan.dal.DashboardRepository;
import com.perpustakaan.dal.TransactionRepository;
import com.perpustakaan.dal.UserRepository;
import com.perpustakaan.model.Transaction;
import com.perpustakaan.model.User;
import java.util.List;
import java.util.Map;

public class AdminService {
    private final UserRepository userRepo;
    private final DashboardRepository dashboardRepo;
    private final TransactionRepository transRepo;

    public AdminService(UserRepository userRepo, DashboardRepository dashboardRepo, TransactionRepository transRepo) {
        this.userRepo = userRepo;
        this.dashboardRepo = dashboardRepo;
        this.transRepo = transRepo;
    }
    
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<Map.Entry<String, Integer>> getPopularBooksReport() {
        return dashboardRepo.getMostPopularBooks(5);
    }
    
    public List<Map.Entry<User, Integer>> getActiveUsersReport() {
        return dashboardRepo.getMostActiveUsers(5);
    }
    
    public List<Transaction> getOverdueBooksReport() {
        return transRepo.findOverdueBooks();
    }
    
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public String suspendUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) return "User tidak ditemukan.";
        if ("ADMIN".equals(user.getRole())) return "Tidak dapat menonaktifkan akun admin.";
        return userRepo.updateUserStatus(username, "SUSPENDED") ? "User berhasil dinonaktifkan." : "Gagal menonaktifkan user.";
    }

    public String activateUser(String username) {
         User user = userRepo.findByUsername(username);
         if (user == null) return "User tidak ditemukan.";
         return userRepo.updateUserStatus(username, "ACTIVE") ? "User berhasil diaktifkan." : "Gagal mengaktifkan user.";
    }

    public String clearUserFines(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) return "User tidak ditemukan.";
        if (user.getTotalFinesUnpaid() == 0) return "User ini tidak memiliki denda.";
        return userRepo.updateUserFine(user.getId(), 0.0) ? "Denda untuk user '" + username + "' berhasil dihapus." : "Gagal menghapus denda.";
    }
}