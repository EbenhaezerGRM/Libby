package com.perpustakaan.pl.user;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.Transaction;
import com.perpustakaan.model.User;
import com.perpustakaan.pl.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyBorrowsController implements MainController.UserController {
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> bookIdColumn;
    @FXML private TableColumn<Transaction, String> titleColumn; 
    @FXML private TableColumn<Transaction, LocalDateTime> borrowDateColumn;
    @FXML private TableColumn<Transaction, LocalDateTime> dueDateColumn;
    @FXML private TableColumn<Transaction, LocalDateTime> returnDateColumn;
    @FXML private TableColumn<Transaction, Double> fineColumn;
    @FXML private Button returnButton;
    @FXML private Label fineInfoLabel;

    private User currentUser;

    @Override
    public void initUserData(User user) {
        this.currentUser = user;
        loadTransactionData();
        fineInfoLabel.setText(String.format("Total Denda Belum Dibayar: Rp %,.0f", user.getTotalFinesUnpaid()));
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        transactionTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> returnButton.setDisable(newVal == null || newVal.getReturnTime() != null));
    }

    private void setupTableColumns() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowTime"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnTime"));
        fineColumn.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        
        formatDateColumn(borrowDateColumn);
        formatDateColumn(dueDateColumn);
        formatDateColumn(returnDateColumn);
    }
    
    private void formatDateColumn(TableColumn<Transaction, LocalDateTime> column) {
        column.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });
    }

    private void loadTransactionData() {
        transactionTable.setItems(FXCollections.observableArrayList(MainApp.libraryService.getMyTransactions(currentUser.getId())));
    }

    @FXML
    private void handleReturnBook() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String result = MainApp.libraryService.returnBook(currentUser, selected.getBookId());
            showAlert(result);
            currentUser = MainApp.authService.refreshUser(currentUser);
            initUserData(currentUser);
        }
    }
    
    private void showAlert(String message) {
        Alert.AlertType type = message.startsWith("SUKSES") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}