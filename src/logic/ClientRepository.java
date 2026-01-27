package logic;

import data.DBManager;
import models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements IClientRepository {
    private DBManager db = new DBManager();

    @Override
    public boolean isTaken(String field, String value) {
        String sql = "SELECT COUNT(*) FROM clients WHERE " + field + " = ?";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            if (conn == null) return false;
            st.setString(1, value);
            ResultSet rs = st.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean save(Client c) {
        String sql = "INSERT INTO clients(name, email, stage, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            if (conn == null) return false;
            st.setString(1, c.getName());
            st.setString(2, c.getEmail());
            st.setString(3, c.getStage());
            st.setDouble(4, c.getPrice());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Client> findByStage(String stage) {
        String sql = (stage.equals("ALL")) ? "SELECT * FROM clients" : "SELECT * FROM clients WHERE stage = ?";
        List<Client> clients = new ArrayList<>();
        Connection conn = db.getConnection();

        if (conn == null) return clients;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            if (!stage.equals("ALL")) st.setString(1, stage.toLowerCase());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("stage"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            if (conn == null) return false;
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}