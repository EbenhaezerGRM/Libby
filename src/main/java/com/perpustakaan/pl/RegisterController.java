package com.perpustakaan.pl;

import com.perpustakaan.main.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label infoLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            infoLabel.setText("Semua field harus diisi.");
            return;
        }
        if (!username.matches("[a-zA-Z0-9]+")) {
            infoLabel.setText("Username hanya boleh berisi huruf dan angka.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            infoLabel.setText("Password dan konfirmasi password tidak cocok.");
            return;
        }

        boolean success = MainApp.authService.register(username, password);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil", "Akun Anda telah berhasil dibuat. Silakan login.");
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.close();
        } else {
            infoLabel.setText("Registrasi gagal. Cek kembali input atau username mungkin sudah ada.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}