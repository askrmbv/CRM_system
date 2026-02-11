package controllers.interfaces;

import models.Task;
import models.Role;
import java.util.List;

public interface ITaskController {
    String addTask(String name, int clientId, int categoryId, int userId, Role role);
    List<Task> getAll();
    List<Task> getByClientId(int clientId);
    boolean updateTask(int id, String name, int categoryId);
    String deleteTask(int id, int userId, Role role);
}
