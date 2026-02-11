package repository;

import data.DBManager;
import models.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository implements ITaskRepository {
    private final DBManager db = DBManager.getInstance();

    @Override
    public boolean save(Task task) {
        String sql = "INSERT INTO tasks(name, customer_id, category_id) VALUES(?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, task.getName());
            st.setInt(2, task.getCustomerId());

            if (task.getCategoryId() > 0) {
                st.setInt(3, task.getCategoryId());
            } else {
                st.setNull(3, Types.INTEGER);
            }

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Task save error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Task> getAll() {
        String sql = """
            SELECT t.id, t.name, t.customer_id, t.category_id,
                   c.full_name as customer_name,
                   cat.name as category_name
            FROM tasks t
            LEFT JOIN customers c ON t.customer_id = c.id
            LEFT JOIN categories cat ON t.category_id = cat.id
            ORDER BY t.id ASC
        """;

        List<Task> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("customer_id"),
                        rs.getInt("category_id")
                );
                task.setCustomerName(rs.getString("customer_name"));
                task.setCategoryName(rs.getString("category_name"));
                list.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Task> getByClientId(int clientId) {
        String sql = """
            SELECT t.id, t.name, t.customer_id, t.category_id,
                   c.full_name as customer_name,
                   cat.name as category_name
            FROM tasks t
            LEFT JOIN customers c ON t.customer_id = c.id
            LEFT JOIN categories cat ON t.category_id = cat.id
            WHERE t.customer_id = ?
            ORDER BY t.id ASC
        """;

        List<Task> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, clientId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("customer_id"),
                        rs.getInt("category_id")
                );
                task.setCustomerName(rs.getString("customer_name"));
                task.setCategoryName(rs.getString("category_name"));
                list.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(int id, String name, int categoryId) {
        String sql = "UPDATE tasks SET name=?, category_id=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, name);

            if (categoryId > 0) {
                st.setInt(2, categoryId);
            } else {
                st.setNull(2, Types.INTEGER);
            }

            st.setInt(3, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Lambda expression example 1: Get tasks by category
    public List<Task> getTasksByCategory(int categoryId) {
        return getAll().stream()
                .filter(task -> task.getCategoryId() == categoryId)  // Lambda expression
                .toList();
    }

    // Lambda expression example 2: Count tasks per customer
    public long countTasksByCustomer(int customerId) {
        return getAll().stream()
                .filter(task -> task.getCustomerId() == customerId)  // Lambda expression
                .count();
    }

    // Lambda expression example 3: Check if customer has any tasks
    public boolean customerHasTasks(int customerId) {
        return getAll().stream()
                .anyMatch(task -> task.getCustomerId() == customerId);  // Lambda expression
    }
}
