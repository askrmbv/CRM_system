package repository;

import data.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Repository for working with clients (Single Responsibility Principle)
public class ClientRepository implements IClientRepository {
    private final DBManager db = DBManager.getInstance();  // Using Singleton

    @Override
    public boolean save(Client c) {
        String sql = "INSERT INTO customers(full_name, email, deal_info, price, task_id) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getName());
            st.setString(2, c.getEmail());
            st.setInt(3, c.getDealStage());
            st.setDouble(4, c.getPrice());
            // Handle task_id: if 0 or negative, insert NULL
            if (c.getTaskId() > 0) {
                st.setInt(5, c.getTaskId());
            } else {
                st.setNull(5, Types.INTEGER);
            }
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Client> getAll() {
        String sql = "SELECT c.id, c.full_name, c.email, c.deal_info, c.price, c.task_id, t.name AS task_name " +
                "FROM customers c LEFT JOIN tasks t ON c.task_id = t.id ORDER BY c.id ASC";
        List<Client> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getInt("deal_info"),
                        rs.getDouble("price"),
                        rs.getInt("task_id")
                );
                client.setTaskName(rs.getString("task_name"));
                list.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(int id, String name, String email, int stage, double price, int taskId) {
        String sql = "UPDATE customers SET full_name=?, email=?, deal_info=?, price=?, task_id=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, name);
            st.setString(2, email);
            st.setInt(3, stage);
            st.setDouble(4, price);
            // Handle task_id: if 0 or negative, set NULL
            if (taskId > 0) {
                st.setInt(5, taskId);
            } else {
                st.setNull(5, Types.INTEGER);
            }
            st.setInt(6, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public String getFullClientDetails(int clientId) {
        String sql = """
            SELECT 
                c.id, c.full_name, c.email, c.deal_info, c.price,
                t.id as task_id, t.name as task_name,
                cat.name as category_name
            FROM customers c
            LEFT JOIN tasks t ON c.task_id = t.id
            LEFT JOIN categories cat ON t.category_id = cat.id
            WHERE c.id = ?
        """;

        StringBuilder details = new StringBuilder();
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, clientId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                details.append("═══════════════════════════════════════════════════\n");
                details.append("                CLIENT DETAILS\n");
                details.append("═══════════════════════════════════════════════════\n\n");

                details.append(String.format("ID:           %d\n", rs.getInt("id")));
                details.append(String.format("Name:         %s\n", rs.getString("full_name")));
                details.append(String.format("Email:        %s\n", rs.getString("email")));

                int stage = rs.getInt("deal_info");
                String stageText = switch(stage) {
                    case 2 -> "Negotiation";
                    case 3 -> "Decision";
                    case 4 -> "Deal";
                    default -> "Lid";
                };
                details.append(String.format("Deal Stage:   %s (%d)\n", stageText, stage));
                details.append(String.format("Price:        $%.2f\n\n", rs.getDouble("price")));

                details.append("Task Information:\n");
                details.append("─────────────────────────────────────────────────\n");
                if (rs.getObject("task_id") != null) {
                    details.append(String.format("Task ID:      %d\n", rs.getInt("task_id")));
                    details.append(String.format("Task Name:    %s\n", rs.getString("task_name")));
                    if (rs.getString("category_name") != null) {
                        details.append(String.format("Category:     %s\n", rs.getString("category_name")));
                    }
                } else {
                    details.append("No task assigned\n");
                }

                details.append("═══════════════════════════════════════════════════\n");
            } else {
                details.append("Client not found!");
            }

        } catch (SQLException e) {
            details.append("Error loading details: ").append(e.getMessage());
        }

        return details.toString();
    }
}
