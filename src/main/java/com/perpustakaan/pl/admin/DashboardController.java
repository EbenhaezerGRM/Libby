package com.perpustakaan.pl.admin;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.Transaction;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;

public class DashboardController {

    @FXML private VBox popularBooksBox;
    @FXML private TableView<Transaction> overdueTable;
    @FXML private TableColumn<Transaction, String> overdueBookIdColumn;
    @FXML private TableColumn<Transaction, String> overdueTitleColumn;
    @FXML private TableColumn<Transaction, String> overdueUserColumn;
    @FXML private TableColumn<Transaction, LocalDateTime> overdueDueDateColumn;

    @FXML
    public void initialize() {
        loadPopularBooks();
        setupOverdueTable();
        loadOverdueBooks();
    }

    private void loadPopularBooks() {
        popularBooksBox.getChildren().clear();
        MainApp.adminService.getPopularBooksReport().forEach(entry -> {
            String text = String.format("- %s (%d kali dipinjam)", entry.getKey(), entry.getValue());
            popularBooksBox.getChildren().add(new Label(text));
        });
    }

    private void setupOverdueTable() {
        overdueBookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        overdueTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        overdueUserColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        overdueDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
    }

    private void loadOverdueBooks() {
        overdueTable.setItems(FXCollections.observableArrayList(MainApp.adminService.getOverdueBooksReport()));
    }
}