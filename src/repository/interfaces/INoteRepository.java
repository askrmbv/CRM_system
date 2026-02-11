package repository.interfaces;

import models.Note;
import java.util.List;

public interface INoteRepository {
    boolean save(Note note);
    List<Note> getByCustomerId(int customerId);
    boolean delete(int noteId);
}
