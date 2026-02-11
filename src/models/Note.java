package models;

import java.sql.Timestamp;

public class Note {
    private int id;
    private int customerId;
    private int userId;
    private String username;  // For display
    private String noteText;
    private Timestamp createdAt;

    public Note(int id, int customerId, int userId, String noteText, Timestamp createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.userId = userId;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }

    // Constructor for creating new note
    public Note(int customerId, int userId, String noteText) {
        this.customerId = customerId;
        this.userId = userId;
        this.noteText = noteText;
    }

    // Getters
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getNoteText() { return noteText; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setter
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s",
                createdAt != null ? createdAt.toString().substring(0, 19) : "Now",
                username != null ? username : "User#" + userId,
                noteText);
    }
}