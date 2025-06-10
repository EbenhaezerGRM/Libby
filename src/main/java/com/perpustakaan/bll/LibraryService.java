package com.perpustakaan.bll;

import com.perpustakaan.dal.*;
import com.perpustakaan.model.*;
import com.perpustakaan.util.IdGenerator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LibraryService {
    private final BookRepository bookRepo;
    private final TransactionRepository transRepo;
    private final ReservationRepository reserveRepo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    public static final int MAX_BORROW_LIMIT = 3;
    public static final int BORROW_DURATION_DAYS = 14;
    public static final double FINE_PER_DAY = 1000.0;

    public LibraryService(BookRepository br, TransactionRepository tr, ReservationRepository rr, ReviewRepository revr, UserRepository ur) {
        this.bookRepo = br;
        this.transRepo = tr;
        this.reserveRepo = rr;
        this.reviewRepo = revr;
        this.userRepo = ur;
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
    public Book getBookById(String bookId) {
        return bookRepo.findById(bookId);
    }
    public List<Review> getReviewsByBookId(String bookId) {
        return reviewRepo.findByBookId(bookId);
    }
    public List<Transaction> getMyTransactions(int userId) {
        return transRepo.findByUserId(userId);
    }
    public List<Transaction> getMyOpenTransactions(int userId) {
        return transRepo.findOpenByUserId(userId);
    }
    public int getBorrowedCountForBook(String bookId) {
        return transRepo.countOpenTransactionsForBook(bookId);
    }
    public List<Reservation> getMyRelevantReservations(int userId) {
        return reserveRepo.findRelevantByUserId(userId);
    }
    public List<Reservation> getReadyReservationsForUser(int userId) {
        return reserveRepo.findReadyForPickupByUserId(userId);
    }
    public Reservation getReadyReservationForUserAndBook(int userId, String bookId) {
        return reserveRepo.findReadyForPickupByUserAndBook(userId, bookId);
    }

    public String borrowBook(User user, String bookId) {
        if (transRepo.countOpenTransactionsByUserId(user.getId()) >= MAX_BORROW_LIMIT) {
            return "Gagal: Batas maksimal peminjaman (" + MAX_BORROW_LIMIT + " buku) tercapai.";
        }
        if (user.getTotalFinesUnpaid() > 0) {
            return "Gagal: Anda memiliki denda yang belum dibayar. Harap selesaikan denda Anda terlebih dahulu.";
        }
        if (transRepo.findOpenTransactionByBookAndUser(bookId, user.getId()) != null) {
            return "Gagal: Anda sudah meminjam buku dengan judul ini.";
        }

        Book book = bookRepo.findById(bookId);
        if (book == null) return "Gagal: Buku tidak ditemukan.";

        Reservation readyReservation = reserveRepo.findReadyForPickupByUserAndBook(user.getId(), bookId);
        if (readyReservation != null) {
            transRepo.save(bookId, user.getId(), LocalDateTime.now().plusDays(BORROW_DURATION_DAYS));
            reserveRepo.updateStatus(readyReservation.getId(), "FULFILLED");
            return "SUKSES: Berhasil meminjam buku '" + book.getTitle() + "' dari reservasi Anda.";
        }

        if (!book.isAvailable()) {
            return "STOK_HABIS";
        }
        
        bookRepo.updateStock(bookId, book.getAvailableStock() - 1);
        transRepo.save(bookId, user.getId(), LocalDateTime.now().plusDays(BORROW_DURATION_DAYS));
        return "SUKSES: Buku '" + book.getTitle() + "' berhasil dipinjam.";
    }

    public String returnBook(User user, String bookId) {
        Transaction trans = transRepo.findOpenTransactionByBookAndUser(bookId, user.getId());
        if (trans == null) return "Gagal: Anda tidak tercatat sedang meminjam buku ini.";

        long daysLate = ChronoUnit.DAYS.between(trans.getDueDate(), LocalDateTime.now());
        double fine = (daysLate > 0) ? daysLate * FINE_PER_DAY : 0.0;
        
        transRepo.updateReturn(trans.getId(), fine);
        
        String response;
        if (fine > 0) {
            User freshUser = userRepo.findById(user.getId());
            userRepo.updateUserFine(user.getId(), freshUser.getTotalFinesUnpaid() + fine);
            response = "SUKSES: Buku berhasil dikembalikan dengan denda keterlambatan Rp " + String.format("%,.0f", fine);
        } else {
            response = "SUKSES: Buku berhasil dikembalikan tepat waktu.";
        }
        
        Reservation nextInQueue = reserveRepo.findNextInQueue(bookId);
        if (nextInQueue != null) {
            reserveRepo.updateStatus(nextInQueue.getId(), "READY_FOR_PICKUP");
            response += "\nINFO: Buku ini sekarang ditahan untuk user '" + nextInQueue.getUsername() + "'.";
        } else {
            Book book = bookRepo.findById(bookId);
            bookRepo.updateStock(bookId, book.getAvailableStock() + 1);
        }
        return response;
    }

    public String createReservation(User user, String bookId) {
        Book book = bookRepo.findById(bookId);
        if (book == null) return "Gagal: Buku tidak ditemukan.";
        if (book.isAvailable()) return "Gagal: Buku ini masih tersedia dan tidak perlu direservasi.";
        if (transRepo.findOpenTransactionByBookAndUser(bookId, user.getId()) != null) return "Gagal: Anda sedang meminjam buku ini.";
        if (reserveRepo.findActiveByUserAndBook(user.getId(), bookId) != null) return "Gagal: Anda sudah memiliki reservasi aktif untuk buku ini.";

        if (reserveRepo.save(bookId, user.getId())) {
            return "SUKSES: Anda telah masuk dalam antrean reservasi untuk buku '" + book.getTitle() + "'.";
        } else {
            return "Gagal: Anda sudah memiliki reservasi aktif untuk buku ini.";
        }
    }
    
    public String cancelReservation(int reservationId) {
        if (reserveRepo.updateStatus(reservationId, "CANCELLED")) {
            return "SUKSES: Reservasi berhasil dibatalkan.";
        }
        return "Gagal: Terjadi kesalahan saat membatalkan reservasi.";
    }

    public String addReview(User user, String bookId, int rating, String comment) {
        if (bookRepo.findById(bookId) == null) return "Gagal: Buku tidak ditemukan.";
        if (reviewRepo.save(bookId, user.getId(), rating, comment)) {
            return "SUKSES: Ulasan Anda berhasil disimpan. Terima kasih!";
        }
        return "Gagal: Terjadi kesalahan saat menyimpan ulasan.";
    }
    
    public String addBook(String title, String author, int year, String desc, String category, int stock) {
        String newId = IdGenerator.generateUniqueBookId(bookRepo);
        Book book = new Book(newId, title, author, year, desc, category, stock, stock);
        return bookRepo.save(book) ? "SUKSES: Buku baru berhasil ditambahkan dengan ID: " + newId : "Gagal menyimpan buku.";
    }

    public String updateBook(String id, String title, String author, int year, String desc, String category, int totalStock, int availableStock) {
        Book book = new Book(id, title, author, year, desc, category, totalStock, availableStock);
        return bookRepo.update(book) ? "SUKSES: Data buku berhasil diupdate." : "Gagal mengupdate data buku.";
    }

    public String deleteBook(String bookId) {
        return bookRepo.delete(bookId) ? "SUKSES: Buku berhasil dihapus." : "Gagal menghapus buku.";
    }
}