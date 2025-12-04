package com.example;

import java.sql.*;

public class DatabaseConnector {
    private static final String DATABASE_URL = "jdbc:sqlite:resources/databases/RestaurantDatabase.db";

    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
            return connection;
        }
}
