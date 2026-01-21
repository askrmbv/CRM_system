package data;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/crm_db", "postgres", "password");
        } catch (Exception e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            return null;
        }
    }
}