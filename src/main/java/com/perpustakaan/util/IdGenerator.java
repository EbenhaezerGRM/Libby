package com.perpustakaan.util;

import com.perpustakaan.dal.BookRepository;
import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateUniqueBookId(BookRepository bookRepository) {
        String id;
        do {
            int numbers = random.nextInt(1000);
            char letter1 = CHARS.charAt(random.nextInt(CHARS.length()));
            char letter2 = CHARS.charAt(random.nextInt(CHARS.length()));
            id = String.format("%03d%C%C", numbers, letter1, letter2);
        } while (bookRepository.findById(id) != null);
        return id;
    }
}