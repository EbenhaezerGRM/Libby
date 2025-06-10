package com.perpustakaan.dal;
import java.sql.Connection;

public interface DatabaseConnector {
    Connection getConnection();
    void closeConnection();
}