package repository;

import data.DBManager;
import repository.interfaces.INoteRepository;
import models.Note;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteRepository implements INoteRepository {
    private final DBManager db = DBManager.getInstance();

    @Override
    public boolean save(Note note) {
        String sql = "INSERT INTO notes(customer_id, user_id, note_text) VALUES(?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, note.getCustomerId());
            st.setInt(2, note.getUserId());
            st.setString(3, note.getNoteText());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Save note error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Note> getByCustomerId(int customerId) {
        String sql = """
            SELECT n.id, n.customer_id, n.user_id, n.note_text, n.created_at, u.username
            FROM notes n
            LEFT JOIN users u ON n.user_id = u.id
            WHERE n.customer_id = ?
            ORDER BY n.created_at DESC
        """;
        List<Note> notes = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, customerId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Note note = new Note(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("user_id"),
                        rs.getString("note_text"),
                        rs.getTimestamp("created_at")
                );
                note.setUsername(rs.getString("username"));
                notes.add(note);
            }
        } catch (SQLException e) {
            System.out.println("Get notes error: " + e.getMessage());
        }
        return notes;
    }

    @Override
    public boolean delete(int noteId) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, noteId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete note error: " + e.getMessage());
            return false;
        }
    }
}
