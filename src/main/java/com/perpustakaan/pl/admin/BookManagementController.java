package com.perpustakaan.pl.admin;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.Book;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.Year;
import java.util.Optional;

public class BookManagementController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, Integer> totalStockColumn;
    @FXML private TableColumn<Book, Integer> availableStockColumn;
    
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadBooks();

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            editButton.setDisable(!isSelected);
            deleteButton.setDisable(!isSelected);
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalStockColumn.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        availableStockColumn.setCellValueFactory(new PropertyValueFactory<>("availableStock"));
    }

    private void loadBooks() {
        bookTable.setItems(FXCollections.observableArrayList(MainApp.libraryService.getAllBooks()));
    }

    @FXML
    private void handleAddBook() {
        showBookDialog(null);
    }

    @FXML
    private void handleEditBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            showBookDialog(selectedBook);
        } else {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Silakan pilih buku yang ingin diedit.");
        }
    }

    @FXML
    private void handleDeleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Apakah Anda yakin ingin menghapus buku '" + selectedBook.getTitle() + "'?", ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText(null);

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    String result = MainApp.libraryService.deleteBook(selectedBook.getId());
                    showAlert(result.startsWith("SUKSES") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Hasil Hapus", result);
                    loadBooks(); 
                }
            });
        } else {
             showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Silakan pilih buku yang ingin dihapus.");
        }
    }

    /**
     * Metode utama untuk menampilkan dialog Tambah/Edit Buku.
     * @param book Objek buku yang akan diedit, atau null jika ingin menambah buku baru.
     */
    
    private void showBookDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle(book == null ? "Tambah Buku Baru" : "Edit Buku");
        dialog.setHeaderText(book == null ? "Isi detail untuk buku baru." : "Ubah detail buku yang sudah ada.");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Judul");
        TextField authorField = new TextField();
        authorField.setPromptText("Penulis");
        TextField yearField = new TextField();
        yearField.setPromptText("Contoh: 2024");
        ComboBox<String> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList("Science", "Social"));
        categoryComboBox.setPromptText("Pilih Kategori");
        TextField stockField = new TextField();
        stockField.setPromptText("Jumlah Stok");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Deskripsi singkat buku");
        descriptionArea.setWrapText(true);

        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            yearField.setText(String.valueOf(book.getPublicationYear()));
            categoryComboBox.setValue(book.getCategory());
            stockField.setText(String.valueOf(book.getTotalStock()));
            descriptionArea.setText(book.getDescription());
        }

        grid.add(new Label("Judul:"), 0, 0); grid.add(titleField, 1, 0);
        grid.add(new Label("Penulis:"), 0, 1); grid.add(authorField, 1, 1);
        grid.add(new Label("Tahun Terbit:"), 0, 2); grid.add(yearField, 1, 2);
        grid.add(new Label("Kategori:"), 0, 3); grid.add(categoryComboBox, 1, 3);
        grid.add(new Label("Stok Total:"), 0, 4); grid.add(stockField, 1, 4);
        grid.add(new Label("Deskripsi:"), 0, 5); grid.add(descriptionArea, 1, 5);

        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String titleText = titleField.getText().trim();
                    String authorText = authorField.getText().trim();
                    String categoryText = categoryComboBox.getValue();
                    String descText = descriptionArea.getText().trim();

                    if (titleText.isEmpty() || authorText.isEmpty() || categoryText == null) {
                        showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Judul, Penulis, dan Kategori tidak boleh kosong.");
                        return null; 
                    }

                    int yearInt = Integer.parseInt(yearField.getText().trim());
                    if (yearInt < 1500 || yearInt > Year.now().getValue() + 1) { 
                        showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Tahun terbit tidak valid.");
                        return null;
                    }

                    int stockInt = Integer.parseInt(stockField.getText().trim());
                    if (stockInt < 0) {
                        showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Stok total tidak boleh negatif.");
                        return null;
                    }

                    if (book != null) { 
                        int borrowedCount = MainApp.libraryService.getBorrowedCountForBook(book.getId());
                        if (stockInt < borrowedCount) {
                            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Stok total tidak boleh lebih kecil dari jumlah yang sedang dipinjam (" + borrowedCount + ").");
                            return null;
                        }
                    }
                    return new Book(
                        book != null ? book.getId() : "", titleText, authorText, yearInt, descText, categoryText, stockInt,
                        book != null ? book.getAvailableStock() : stockInt 
                    );
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Tahun dan Stok harus berupa angka yang valid.");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();

        result.ifPresent(editedBook -> {
            String serviceResult;
            if (book == null) { 
                serviceResult = MainApp.libraryService.addBook(editedBook.getTitle(), editedBook.getAuthor(), editedBook.getPublicationYear(), editedBook.getDescription(), editedBook.getCategory(), editedBook.getTotalStock());
            } else {
                int stockDifference = editedBook.getTotalStock() - book.getTotalStock();
                int newAvailableStock = book.getAvailableStock() + stockDifference;
                serviceResult = MainApp.libraryService.updateBook(editedBook.getId(), editedBook.getTitle(), editedBook.getAuthor(), editedBook.getPublicationYear(), editedBook.getDescription(), editedBook.getCategory(), editedBook.getTotalStock(), newAvailableStock);
            }
            showAlert(Alert.AlertType.INFORMATION, "Sukses", serviceResult);
            loadBooks(); 
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}