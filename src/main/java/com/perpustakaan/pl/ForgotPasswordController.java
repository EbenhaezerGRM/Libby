package com.perpustakaan.pl;

import com.perpustakaan.main.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label infoLabel;

    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            infoLabel.setText("Semua field harus diisi.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            infoLabel.setText("Password baru dan konfirmasi tidak cocok.");
            return;
        }

        boolean success = MainApp.authService.resetPassword(username, newPassword);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Reset Berhasil", "Password untuk akun '" + username + "' telah berhasil diubah.");
            Stage stage = (Stage) infoLabel.getScene().getWindow();
            stage.close();
        } else {
            infoLabel.setText("Gagal mereset password. Periksa kembali input Anda.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}