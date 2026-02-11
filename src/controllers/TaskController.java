package controllers;

import controllers.interfaces.ITaskController;
import repository.interfaces.IActivityLogRepository;
import repository.interfaces.IUserRepository;
import models.Task;
import models.Client;
import models.Role;
import exceptions.AccessDeniedException;
import exceptions.InvalidDataException;
import java.util.List;

public class TaskController implements ITaskController {
    private final ITaskRepository taskRepo;
    private final IActivityLogRepository activityLog;
    private final IClientRepository clientRepo;
    private final IUserRepository userRepo;

    public TaskController(ITaskRepository taskRepo, IActivityLogRepository activityLog, IClientRepository clientRepo, IUserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.activityLog = activityLog;
        this.clientRepo = clientRepo;
        this.userRepo = userRepo;
    }

    private String getUsernameById(int userId) {
        return userRepo.getUsernameById(userId);
    }

    @Override
    public String addTask(String name, int clientId, int categoryId, int userId, Role role) {
        // EDITOR can only view and edit, not add
        if (role == Role.EDITOR) {
            throw new AccessDeniedException("EDITOR can only view existing tasks, not add new ones");
        }

        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Task name cannot be empty");
        }

        // Check if client exists
        Client client = clientRepo.getById(clientId);
        if (client == null) {
            throw new InvalidDataException("Client with ID " + clientId + " not found");
        }

        Task task = new Task(0, name, clientId, categoryId);

        if (taskRepo.save(task)) {
            // Log activity with username
            activityLog.log(userId, "ADD_TASK",
                    String.format("%s добавил(а) task \"%s\" для клиента %s [ID:%d]",
                            getUsernameById(userId), name, client.getName(), clientId));
            return "✓ Task added successfully!";
        }
        return "✗ Error adding task";
    }

    @Override
    public List<Task> getAll() {
        return taskRepo.getAll();
    }

    @Override
    public List<Task> getByClientId(int clientId) {
        return taskRepo.getByClientId(clientId);
    }

    @Override
    public boolean updateTask(int id, String name, int categoryId) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Task name cannot be empty");
        }
        return taskRepo.update(id, name, categoryId);
    }

    @Override
    public String deleteTask(int id, int userId, Role role) {
        // Only ADMIN can delete tasks
        if (role != Role.ADMIN) {
            throw new AccessDeniedException("Only Admin can delete tasks");
        }

        if (taskRepo.delete(id)) {
            // Log activity
            activityLog.log(userId, "DELETE_TASK",
                    String.format("%s удалил(а) task [ID:%d]", getUsernameById(userId), id));
            return "✓ Task deleted";
        }
        return "✗ Error deleting task";
    }
}
