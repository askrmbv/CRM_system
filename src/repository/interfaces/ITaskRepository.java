package repository.interfaces;

import java.util.List;

public interface ITaskRepository {
    boolean save(Task task);
    List<Task> getAll();
    boolean update(int id, String name, int categoryId);
    boolean delete(int id);
}
