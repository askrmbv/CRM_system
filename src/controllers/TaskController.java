package logic;

import models.Task;
import java.util.List;

public class TaskController {
    private final ITaskRepository repo;

    public TaskController(ITaskRepository repo) {
        this.repo = repo;
    }

    public boolean addTask(String name, int categoryId) {
        Task task = new Task(0, name, categoryId);
        return repo.save(task);
    }

    public List<Task> getAll() {
        return repo.getAll();
    }

    public boolean updateTask(int id, String name, int categoryId) {
        return repo.update(id, name, categoryId);
    }

    public boolean deleteTask(int id) {
        return repo.delete(id);
    }
}