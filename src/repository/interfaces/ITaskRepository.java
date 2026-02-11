package repository.interfaces;

import models.Task;
import java.util.List;

public interface ITaskRepository {
    boolean save(Task t);
    List<Task> getAll();
    List<Task> getByClientId(int clientId);
    boolean update(int id, String name, int categoryId);
    boolean delete(int id);

    // Lambda methods
    List<Task> getTasksByCategory(int categoryId);
    long countTasksByCustomer(int customerId);
    boolean customerHasTasks(int customerId);
}
