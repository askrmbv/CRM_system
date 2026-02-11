package models;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private int userId;
    private String username;  // For display
    private String actionType;
    private String description;
    private Timestamp createdAt;

    public ActivityLog(int id, int userId, String actionType, String description, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        String userDisplay = (username != null && !username.isEmpty()) ? username : "Unknown";
        return String.format("[%s] %s - %s",
                createdAt.toString().substring(0, 19),
                userDisplay,
                description);
    }
}
