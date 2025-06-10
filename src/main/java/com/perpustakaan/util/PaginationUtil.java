package com.perpustakaan.util;

import java.util.List;
import java.util.Scanner;

public class PaginationUtil {

    public static <T> void displayPaginated(Scanner scanner, List<T> list, int pageSize, String title) {
        if (list == null || list.isEmpty()) {
            System.out.println("Tidak ada data untuk ditampilkan.");
            return;
        }

        int totalPages = (int) Math.ceil((double) list.size() / pageSize);
        int currentPage = 1;

        while (true) {
            System.out.printf("\n--- %s (Halaman %d dari %d) ---\n", title, currentPage, totalPages);
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, list.size());

            for (int i = startIndex; i < endIndex; i++) {
                System.out.println((i + 1) + ". " + list.get(i).toString());
            }

            if (totalPages <= 1) break;

            System.out.println("\n---------------------------------");
            System.out.print("Navigasi: [N]ext, [P]rev, [E]xit > ");
            String command = scanner.nextLine().trim().toUpperCase();

            if (command.equals("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Anda sudah di halaman terakhir.");
                }
            } else if (command.equals("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Anda sudah di halaman pertama.");
                }
            } else if (command.equals("E")) {
                break;
            } else {
                System.out.println("Perintah tidak valid.");
            }
        }
    }
}