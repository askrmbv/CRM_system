package repository;

import data.DBManager;
import repository.interfaces.IActivityLogRepository;
import models.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepository implements IActivityLogRepository {
    private final DBManager db = DBManager.getInstance();

    @Override
    public boolean log(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_log(user_id, action_type, description) VALUES(?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, userId);
            st.setString(2, actionType);
            st.setString(3, description);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Activity log error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ActivityLog> getAll() {
        String sql = """
            SELECT al.id, al.user_id, al.action_type, al.description, al.created_at,
                   u.username
            FROM activity_log al
            LEFT JOIN users u ON al.user_id = u.id
            ORDER BY al.created_at DESC
        """;

        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ActivityLog log = new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                log.setUsername(rs.getString("username"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ActivityLog> getRecent(int limit) {
        String sql = """
            SELECT al.id, al.user_id, al.action_type, al.description, al.created_at,
                   u.username
            FROM activity_log al
            LEFT JOIN users u ON al.user_id = u.id
            ORDER BY al.created_at DESC
            LIMIT ?
        """;

        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, limit);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ActivityLog log = new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                log.setUsername(rs.getString("username"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lambda expression example 1: Filter logs by action type
    public List<ActivityLog> getLogsByActionType(String actionType) {
        return getAll().stream()
                .filter(log -> log.getActionType().equals(actionType))  // Lambda expression
                .toList();
    }

    // Lambda expression example 2: Get logs by specific user
    public List<ActivityLog> getLogsByUserId(int userId) {
        return getAll().stream()
                .filter(log -> log.getUserId() == userId)  // Lambda expression
                .toList();
    }

    // Lambda expression example 3: Count actions by type
    public long countActionsByType(String actionType) {
        return getAll().stream()
                .filter(log -> log.getActionType().equals(actionType))  // Lambda expression
                .count();
    }

    // Lambda expression example 4: Get all unique action types
    public List<String> getAllActionTypes() {
        return getAll().stream()
                .map(ActivityLog::getActionType)  // Method reference (lambda!)
                .distinct()
                .sorted()
                .toList();
    }
}
