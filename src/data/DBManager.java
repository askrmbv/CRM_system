package data;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    public Connection getConnection() {
        String url = "jdbc:postgresql://localhost:5432/crm_db";
        String user = "postgres";
        String pass = "0000";
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}