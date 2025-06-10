package com.perpustakaan.pl.admin;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, Double> finesColumn;

    @FXML private Button suspendButton;
    @FXML private Button activateButton;
    @FXML private Button clearFinesButton;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                boolean isSuspended = "SUSPENDED".equals(newSelection.getStatus());
                suspendButton.setDisable(isSuspended);
                activateButton.setDisable(!isSuspended);
                clearFinesButton.setDisable(newSelection.getTotalFinesUnpaid() == 0);
            } else {
                suspendButton.setDisable(true);
                activateButton.setDisable(true);
                clearFinesButton.setDisable(true);
            }
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        finesColumn.setCellValueFactory(new PropertyValueFactory<>("totalFinesUnpaid"));
    }

    private void loadUsers() {
        userTable.setItems(FXCollections.observableArrayList(MainApp.adminService.getAllUsers()));
    }

    @FXML
    private void handleSuspend() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String result = MainApp.adminService.suspendUser(selectedUser.getUsername());
            showAlert(result);
            loadUsers();
        }
    }

    @FXML
    private void handleActivate() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String result = MainApp.adminService.activateUser(selectedUser.getUsername());
            showAlert(result);
            loadUsers();
        }
    }

    @FXML
    private void handleClearFines() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String result = MainApp.adminService.clearUserFines(selectedUser.getUsername());
            showAlert(result);
            loadUsers();
        }
    }

    private void showAlert(String message) {
        Alert.AlertType type = message.contains("Gagal") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}