package logic;

import data.DBManager;
import models.Client;
import java.sql.*;

public class ClientRepository {
    private DBManager db = new DBManager();

    public boolean save(Client c) {
        String sql = "INSERT INTO clients(name, email, status, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getName());
            st.setString(2, c.getEmail());
            st.setInt(3, c.getStatus());
            st.setDouble(4, c.getPrice());
            return st.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM clients WHERE name = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public void findAll() {
        String sql = "SELECT * FROM clients";
        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(new Client(
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("email"), rs.getInt("status"), rs.getDouble("price")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}