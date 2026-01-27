package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static Connection connection;

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "postgres";
                String pass = "0000";

                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                } catch (ClassNotFoundException e) {
                    System.out.println("Driver not found: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("DB Connection Error: " + e.getMessage());
        }
        return connection;
    }
}