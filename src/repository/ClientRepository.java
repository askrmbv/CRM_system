package repository;

import data.DBManager;
import models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements IClientRepository {
    private final DBManager db = DBManager.getInstance();

    @Override
    public boolean save(Client c) {
        String sql = "INSERT INTO customers(full_name, email, deal_info, price, note) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getName());
            st.setString(2, c.getEmail());
            st.setInt(3, c.getDealStage());
            st.setDouble(4, c.getPrice());

            // Handle note: if empty or null, insert NULL
            if (c.getNote() != null && !c.getNote().trim().isEmpty()) {
                st.setString(5, c.getNote());
            } else {
                st.setNull(5, Types.VARCHAR);
            }

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Client> getAll() {
        String sql = "SELECT id, full_name, email, deal_info, price, note FROM customers ORDER BY id ASC";
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
                        rs.getString("note")
                );
                list.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lambda expression example 1: Filter clients by minimum price
    public List<Client> getClientsByMinPrice(double minPrice) {
        return getAll().stream()
                .filter(client -> client.getPrice() >= minPrice)  // Lambda expression
                .toList();
    }

    // Lambda expression example 2: Get clients by deal stage
    public List<Client> getClientsByStage(int stage) {
        return getAll().stream()
                .filter(client -> client.getDealStage() == stage)  // Lambda expression
                .toList();
    }

    // Lambda expression example 3: Sort clients by price descending
    public List<Client> getClientsSortedByPrice() {
        return getAll().stream()
                .sorted((c1, c2) -> Double.compare(c2.getPrice(), c1.getPrice()))  // Lambda expression
                .toList();
    }

    // Lambda expression example 4: Get total revenue
    public double getTotalRevenue() {
        return getAll().stream()
                .mapToDouble(Client::getPrice)  // Method reference (also a lambda!)
                .sum();
    }

    @Override
    public Client getById(int id) {
        String sql = "SELECT id, full_name, email, deal_info, price, note FROM customers WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getInt("deal_info"),
                        rs.getDouble("price"),
                        rs.getString("note")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(int id, String name, String email, int stage, double price, String note) {
        String sql = "UPDATE customers SET full_name=?, email=?, deal_info=?, price=?, note=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, name);
            st.setString(2, email);
            st.setInt(3, stage);
            st.setDouble(4, price);

            // Handle note: if empty or null, set NULL
            if (note != null && !note.trim().isEmpty()) {
                st.setString(5, note);
            } else {
                st.setNull(5, Types.VARCHAR);
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
                c.id, c.full_name, c.email, c.deal_info, c.price, c.note,
                t.id as task_id, t.name as task_name,
                cat.name as category_name
            FROM customers c
            LEFT JOIN tasks t ON c.id = t.customer_id
            LEFT JOIN categories cat ON t.category_id = cat.id
            WHERE c.id = ?
            ORDER BY t.id
        """;

        StringBuilder details = new StringBuilder();
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, clientId);
            ResultSet rs = st.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                if (!hasData) {
                    // Print client info once
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
                    details.append(String.format("Price:        $%.2f\n", rs.getDouble("price")));

                    String note = rs.getString("note");
                    if (note != null && !note.isEmpty()) {
                        details.append(String.format("Note:         %s\n", note));
                    }

                    details.append("\nTasks:\n");
                    details.append("─────────────────────────────────────────────────\n");
                    hasData = true;
                }

                // Print task info
                if (rs.getObject("task_id") != null) {
                    details.append(String.format("  • Task #%d: %s",
                            rs.getInt("task_id"),
                            rs.getString("task_name")));
                    if (rs.getString("category_name") != null) {
                        details.append(String.format(" [%s]", rs.getString("category_name")));
                    }
                    details.append("\n");
                }
            }

            if (!hasData) {
                details.append("Client not found!");
            } else {
                if (!details.toString().contains("Task #")) {
                    details.append("  No tasks assigned\n");
                }
                details.append("═══════════════════════════════════════════════════\n");
            }

        } catch (SQLException e) {
            details.append("Error loading details: ").append(e.getMessage());
        }

        return details.toString();
    }
}
