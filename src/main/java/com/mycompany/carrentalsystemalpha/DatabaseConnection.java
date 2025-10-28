package com.mycompany.carrentalsystemalpha;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/carrental";
    private static final String USER = "root";  // change if you use another username
    private static final String PASSWORD = "";  // put your MySQL password if you set one

    public static Connection connect() {
        try {
            // Load MySQL JDBC Driver (optional for newer versions, but safe to add)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Try to connect
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found. Did you add the connector JAR?");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("❌ Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}
