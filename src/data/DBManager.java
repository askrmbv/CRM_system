package data;

import data.interfaces.IDB;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager implements IDB {
    private static DBManager instance;
    private Connection connection;

    private DBManager() { }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:postgresql://localhost:5432/crm_db";
                String user = "postgres";
                String pass = "0000";

                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, pass);
                // Убрал вывод "✓ Connected!"
            }
        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
