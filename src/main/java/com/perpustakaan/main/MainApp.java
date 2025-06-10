package com.perpustakaan.main;

import com.perpustakaan.bll.AdminService;
import com.perpustakaan.bll.AuthService;
import com.perpustakaan.bll.LibraryService;
import com.perpustakaan.dal.*;
import com.perpustakaan.model.User;
import com.perpustakaan.pl.LoginController;
import com.perpustakaan.pl.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    public static AuthService authService;
    public static LibraryService libraryService;
    public static AdminService adminService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Libby");

        initializeBackend();
        showLoginView();
    }

    private void initializeBackend() {
        SQLiteConnector dbConnector = new SQLiteConnector();
        dbConnector.initializeDatabase();

        BookRepository bookRepo = new BookRepository(dbConnector);
        UserRepository userRepo = new UserRepository(dbConnector);
        TransactionRepository transRepo = new TransactionRepository(dbConnector);
        ReviewRepository reviewRepo = new ReviewRepository(dbConnector);
        ReservationRepository reserveRepo = new ReservationRepository(dbConnector);
        DashboardRepository dashboardRepo = new DashboardRepository(dbConnector);

        authService = new AuthService(userRepo);
        adminService = new AdminService(userRepo, dashboardRepo, transRepo);
        libraryService = new LibraryService(bookRepo, transRepo, reserveRepo, reviewRepo, userRepo);
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/perpustakaan/pl/LoginView.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showMainView(User user) {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/perpustakaan/pl/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.initData(user, this);

            primaryStage.setScene(new Scene(root, 1200, 800)); 
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}