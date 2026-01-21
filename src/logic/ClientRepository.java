package logic;

import data.DBManager;
import models.Client;
import java.sql.*;

public class ClientRepository {
    private DBManager db = new DBManager();

    // Мгновенная проверка уникальности
    public boolean isTaken(String field, String value) {
        String sql = "SELECT COUNT(*) FROM clients WHERE " + field + " = ?";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, value);
            ResultSet rs = st.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean save(Client c) {
        String sql = "INSERT INTO clients(name, email, stage, price, privilege) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getName());
            st.setString(2, c.getEmail());
            st.setString(3, c.getStage());
            st.setDouble(4, c.getPrice());
            st.setString(5, c.getPrivilege());
            return st.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Фильтрация по стадиям
    public void findByStage(String stage) {
        String sql = (stage.equals("ALL")) ? "SELECT * FROM clients" : "SELECT * FROM clients WHERE stage = ?";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            if (!stage.equals("ALL")) st.setString(1, stage.toLowerCase());
            ResultSet rs = st.executeQuery();
            System.out.println("\n--- LIST ---");
            while (rs.next()) {
                System.out.println(new Client(rs.getInt("id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("stage"), rs.getDouble("price")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}