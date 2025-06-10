package com.perpustakaan.pl.user;

import com.perpustakaan.main.MainApp;
import com.perpustakaan.model.Book;
import com.perpustakaan.model.Review;
import com.perpustakaan.model.Reservation;
import com.perpustakaan.model.User;
import com.perpustakaan.pl.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Optional;

public class CatalogController implements MainController.UserController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, Integer> yearColumn;
    @FXML private TableColumn<Book, String> stockColumn;
    @FXML private TableColumn<Book, Double> ratingColumn;
    @FXML private Label detailTitleLabel;
    @FXML private Label detailAuthorLabel;
    @FXML private Label detailCategoryLabel;
    @FXML private TextArea detailDescriptionArea;
    @FXML private ListView<Review> reviewListView;
    @FXML private Button borrowButton;
    @FXML private Button reserveButton;
    @FXML private Button addReviewButton;

    private User currentUser;

    @Override
    public void initUserData(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadBooks();
        bookTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookDetails(newValue));
        showBookDetails(null);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("availableStock"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
    }

    private void loadBooks() {
        bookTable.setItems(FXCollections.observableArrayList(MainApp.libraryService.getAllBooks()));
    }

    private void showBookDetails(Book book) {
        if (book != null) {
            detailTitleLabel.setText(book.getTitle());
            detailAuthorLabel.setText("oleh " + book.getAuthor() + " (" + book.getPublicationYear() + ")");
            detailCategoryLabel.setText("Kategori: " + book.getCategory());
            detailDescriptionArea.setText(book.getDescription());

            List<Review> reviews = MainApp.libraryService.getReviewsByBookId(book.getId());
            reviewListView.setItems(FXCollections.observableArrayList(reviews));
            
            Reservation readyReservation = MainApp.libraryService.getReadyReservationForUserAndBook(currentUser.getId(), book.getId());
            boolean hasReadyReservation = readyReservation != null;

            borrowButton.setDisable(!(book.isAvailable() || hasReadyReservation));
            reserveButton.setDisable(book.isAvailable() || hasReadyReservation);
            addReviewButton.setDisable(false);

        } else {
            detailTitleLabel.setText("Pilih buku untuk melihat detail");
            detailAuthorLabel.setText("");
            detailCategoryLabel.setText("");
            detailDescriptionArea.clear();
            reviewListView.getItems().clear();
            borrowButton.setDisable(true);
            reserveButton.setDisable(true);
            addReviewButton.setDisable(true);
        }
    }

    @FXML
    private void handleBorrow() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            String result = MainApp.libraryService.borrowBook(currentUser, selectedBook.getId());
            showAlert(result);
            loadBooks();
            showBookDetails(MainApp.libraryService.getBookById(selectedBook.getId()));
        }
    }

    @FXML
    private void handleReserve() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            String result = MainApp.libraryService.createReservation(currentUser, selectedBook.getId());
            showAlert(result);
        }
    }
    
    @FXML
    private void handleAddReview() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tulis Ulasan untuk: " + selectedBook.getTitle());

        VBox content = new VBox(10);
        ComboBox<Integer> ratingBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingBox.setPromptText("Pilih Rating Bintang");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Tulis komentar Anda di sini...");
        content.getChildren().addAll(new Label("Rating:"), ratingBox, new Label("Komentar:"), commentArea);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            if (ratingBox.getValue() != null) {
                String reviewResult = MainApp.libraryService.addReview(currentUser, selectedBook.getId(), ratingBox.getValue(), commentArea.getText());
                showAlert(reviewResult);
                loadBooks(); 
                showBookDetails(MainApp.libraryService.getBookById(selectedBook.getId()));
            } else {
                showAlert("Gagal: Rating tidak boleh kosong.");
            }
        }
    }
    
    private void showAlert(String message) {
        Alert.AlertType type = message.startsWith("SUKSES") ? Alert.AlertType.INFORMATION : message.startsWith("Gagal") ? Alert.AlertType.ERROR : Alert.AlertType.WARNING;
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}