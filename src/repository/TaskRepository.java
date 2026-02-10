package repository;

import data.DBManager;
import repository.interfaces.ITaskRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class +TaskRepository implements ITaskRepository {
    private final DBManager db = DBManager.getInstance();

    @Override
    public boolean save(Task task) {
        String sql = "INSERT INTO tasks(name, category_id) VALUES(?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, task.getName());
            // If category_id = 0 or lower, NULL
            if (task.getCategoryId() > 0) {
                st.setInt(2, task.getCategoryId());
            } else {
                st.setNull(2, Types.INTEGER);
            }
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Task> getAll() {
        String sql = "SELECT * FROM tasks ORDER BY id ASC";
        List<Task> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Task(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("category_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Get all error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(int id, String name, int categoryId) {
        String sql = "UPDATE tasks SET name=?, category_id=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, name);
            // If categoryId = 0 or lower,  NULL
            if (categoryId > 0) {
                st.setInt(2, categoryId);
            } else {
                st.setNull(2, Types.INTEGER);
            }
            st.setInt(3, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
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
            System.out.println("Delete error (maybe in use): " + e.getMessage());
            return false;
        }
    }
}
