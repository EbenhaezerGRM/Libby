package com.perpustakaan.pl;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.Reservation;
import com.perpustakaan.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private VBox navigationBox;
    @FXML private StackPane contentPane;
    @FXML private Label statusLabel;

    private User currentUser;
    private MainApp mainApp;

    public interface UserController {
        void initUserData(User user);
    }

    public void initData(User user, MainApp mainApp) {
        this.currentUser = user;
        this.mainApp = mainApp;
        statusLabel.setText("Login sebagai: " + user.getUsername() + " (" + user.getRole() + ")");
        setupNavigation();
        checkUserNotifications();

        if("ADMIN".equals(user.getRole())) {
            loadView("/com/perpustakaan/pl/admin/DashboardView.fxml");
        } else {
            loadView("/com/perpustakaan/pl/user/CatalogView.fxml");
        }
    }

    private void setupNavigation() {
        navigationBox.getChildren().clear();
        if ("ADMIN".equals(currentUser.getRole())) {
            navigationBox.getChildren().add(createNavButton("Dashboard", "/com/perpustakaan/pl/admin/DashboardView.fxml"));
            navigationBox.getChildren().add(createNavButton("Manajemen Buku", "/com/perpustakaan/pl/admin/BookManagementView.fxml"));
            navigationBox.getChildren().add(createNavButton("Manajemen User", "/com/perpustakaan/pl/admin/UserManagementView.fxml"));
        } else {
            navigationBox.getChildren().add(createNavButton("Katalog Buku", "/com/perpustakaan/pl/user/CatalogView.fxml"));
            navigationBox.getChildren().add(createNavButton("Peminjaman Saya", "/com/perpustakaan/pl/user/MyBorrowsView.fxml"));
            navigationBox.getChildren().add(createNavButton("Reservasi Saya", "/com/perpustakaan/pl/user/MyReservationsView.fxml"));
        }
    }

    private Button createNavButton(String text, String fxmlPath) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> loadView(fxmlPath));
        return button;
    }

    private void loadView(String fxmlPath) {
        try {
            java.net.URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("Tidak dapat menemukan file FXML: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof UserController) {
                ((UserController) controller).initUserData(currentUser);
            }

            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void checkUserNotifications() {
        this.currentUser = MainApp.authService.refreshUser(this.currentUser);
        if (this.currentUser == null) return; 
        
        statusLabel.setText("Login sebagai: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        BorderPane mainPane = (BorderPane) contentPane.getParent();
        VBox messageBox = new VBox(5);
        boolean hasNotif = false;
        
        if (currentUser.getTotalFinesUnpaid() > 0) {
            messageBox.getChildren().add(new Label(String.format("💰 Anda memiliki denda belum dibayar: Rp %,.0f", currentUser.getTotalFinesUnpaid())));
            hasNotif = true;
        }

        List<Reservation> readyReservations = MainApp.libraryService.getReadyReservationsForUser(currentUser.getId());
        if (!readyReservations.isEmpty()) {
            messageBox.getChildren().add(new Label("🔔 Reservasi Anda siap diambil:"));
            readyReservations.forEach(r -> messageBox.getChildren().add(new Label("   - " + r.getBookTitle() + " (ID: "+r.getBookId()+")")));
            hasNotif = true;
        }
        
        if (hasNotif) {
            BorderPane notificationPane = new BorderPane();
            notificationPane.setPadding(new Insets(10));
            notificationPane.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeeba; -fx-background-radius: 5; -fx-border-radius: 5;");

            Button closeButton = new Button("✖");
            closeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #664d03;");
            closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: red;"));
            closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #664d03;"));
            
            closeButton.setOnAction(e -> mainPane.setTop(null));
            notificationPane.setCenter(messageBox);
            notificationPane.setRight(closeButton);
            mainPane.setTop(notificationPane);
            BorderPane.setMargin(notificationPane, new Insets(0, 0, 10, 0)); 
        } else {
            mainPane.setTop(null); 
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        mainApp.showLoginView();
    }
}