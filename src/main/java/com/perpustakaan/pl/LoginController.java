package com.perpustakaan.pl;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password tidak boleh kosong.");
            return;
        }

        User user = MainApp.authService.login(username, password);

        if (user != null) {
            mainApp.showMainView(user);
        } else {
            errorLabel.setText("Username/password salah atau akun dinonaktifkan.");
            passwordField.clear();
        }
    }
    
    @FXML
    private void handleShowRegisterView() {
        showDialog("RegisterView.fxml", "Register Akun Baru");
    }

    @FXML
    private void handleShowForgotPasswordView() {
        showDialog("ForgotPasswordView.fxml", "Reset Password");
    }
    
    private void showDialog(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}