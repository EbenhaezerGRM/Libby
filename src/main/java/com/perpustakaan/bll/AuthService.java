package com.perpustakaan.bll;

import com.perpustakaan.dal.UserRepository;
import com.perpustakaan.model.User;
import com.perpustakaan.util.PasswordUtil;

public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepository) {
        this.userRepo = userRepository;
    }

    public User refreshUser(User oldUser) {
        if (oldUser == null) return null;
        return userRepo.findById(oldUser.getId());
    }

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && PasswordUtil.checkPassword(password, user.getHashedPassword())) {
            if ("SUSPENDED".equals(user.getStatus())) {
                System.err.println("Login gagal: Akun Anda telah dinonaktifkan (suspended). Silakan hubungi admin.");
                return null;
            }
            return user;
        }
        System.err.println("Login gagal: Username atau password salah.");
        return null;
    }

    public boolean register(String username, String password) {
        if (username.length() < 5 || username.length() > 20) {
            System.err.println("Registrasi gagal: Username harus 5-20 karakter.");
            return false;
        }
        if (password.length() < 5 || password.length() > 20) {
            System.err.println("Registrasi gagal: Password harus 5-20 karakter.");
            return false;
        }
        if (userRepo.findByUsername(username) != null) {
            System.err.println("Registrasi gagal: Username '" + username + "' sudah digunakan.");
            return false;
        }
        return userRepo.save(username, PasswordUtil.hashPassword(password));
    }

    public boolean resetPassword(String username, String newPassword) {
        if (newPassword.length() < 5 || newPassword.length() > 20) {
            System.err.println("Ganti password gagal: Password baru harus 5-20 karakter.");
            return false;
        }
        if (userRepo.findByUsername(username) == null) {
            System.err.println("Ganti password gagal: Username '" + username + "' tidak ditemukan.");
            return false;
        }
        return userRepo.updatePassword(username, PasswordUtil.hashPassword(newPassword));
    }
}