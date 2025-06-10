package com.perpustakaan.pl.user;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.Reservation;
import com.perpustakaan.model.User;
import com.perpustakaan.pl.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MyReservationsController implements MainController.UserController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> bookTitleColumn;
    @FXML private TableColumn<Reservation, LocalDateTime> reservationDateColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private Button cancelButton;

    private User currentUser;

    @Override
    public void initUserData(User user) {
        this.currentUser = user;
        loadReservations();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        reservationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> cancelButton.setDisable(newVal == null || !"ACTIVE".equals(newVal.getStatus())));
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        reservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        formatDateColumn(reservationDateColumn);
    }

    private void loadReservations() {
        reservationTable.setItems(FXCollections.observableArrayList(
                MainApp.libraryService.getMyRelevantReservations(currentUser.getId())));
    }

    @FXML
    private void handleCancelReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Apakah Anda yakin ingin membatalkan reservasi untuk buku '" + selected.getBookTitle() + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Konfirmasi Pembatalan");
            confirmAlert.setHeaderText(null);

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                String serviceResult = MainApp.libraryService.cancelReservation(selected.getId());
                showAlert(serviceResult);
                loadReservations();
            }
        }
    }

    private void formatDateColumn(TableColumn<Reservation, LocalDateTime> column) {
        column.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });
    }
    
    private void showAlert(String message) {
        Alert.AlertType type = message.startsWith("SUKSES") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}