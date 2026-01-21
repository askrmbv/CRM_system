package logic;
import data.DBManager;
import models.Client;
import java.sql.*;

public class ClientRepository {
    private DBManager db = new DBManager();

    public boolean create(Client c) {
        String sql = "INSERT INTO clients(name, email, status, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getName());
            st.setString(2, c.getEmail());
            st.setInt(3, c.getStatus());
            st.setDouble(4, c.getPrice());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }
    }
}


