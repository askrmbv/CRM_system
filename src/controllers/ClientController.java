package controllers;

import controllers.interfaces.IClientController;
import repository.interfaces.IActivityLogRepository;
import repository.interfaces.IUserRepository;
import models.Client;
import models.Role;
import exceptions.AccessDeniedException;
import exceptions.InvalidDataException;
import java.util.List;

public class ClientController implements IClientController {
    private final IClientRepository clientRepo;
    private final IActivityLogRepository activityLog;
    private final IUserRepository userRepo;

    public ClientController(IClientRepository clientRepo, IActivityLogRepository activityLog, IUserRepository userRepo) {
        this.clientRepo = clientRepo;
        this.activityLog = activityLog;
        this.userRepo = userRepo;
    }

    private String getUsernameById(int userId) {
        return userRepo.getUsernameById(userId);
    }

    @Override
    public String addClient(String name, String email, int stage, double price, String note, int userId, Role role) {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Client name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new InvalidDataException("Invalid email format");
        }
        if (stage < 1 || stage > 4) {
            throw new InvalidDataException("Stage must be between 1 and 4");
        }
        if (price < 0) {
            throw new InvalidDataException("Price cannot be negative");
        }

        // Role check - EDITOR can only view and edit, not add
        if (role == Role.EDITOR) {
            throw new AccessDeniedException("EDITOR can only view and edit existing clients, not add new ones");
        }

        // Role check - all can add clients
        Client client = new Client(0, name, email, stage, price, note);

        if (clientRepo.save(client)) {
            // Log activity with username
            activityLog.log(userId, "ADD_CLIENT",
                    String.format("%s добавил(а) клиента %s", getUsernameById(userId), name));
            return "✓ Client added successfully!";
        }
        return "✗ Error adding client";
    }

    @Override
    public List<Client> getAll() {
        return clientRepo.getAll();
    }

    @Override
    public String updateClient(int id, String name, String email, int stage, double price, String note, Role role) {
        // EDITOR can only update existing clients (not add or delete)
        // This gives EDITOR a specific use case!

        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Client name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new InvalidDataException("Invalid email format");
        }

        if (clientRepo.update(id, name, email, stage, price, note)) {
            return "✓ Client updated!";
        }
        return "✗ Client not found";
    }

    @Override
    public String deleteClient(int id, int userId, Role role) {
        // Only ADMIN can delete
        if (role != Role.ADMIN) {
            throw new AccessDeniedException("Only Admin can delete clients");
        }

        // Get client name before deletion for log
        Client client = clientRepo.getById(id);
        if (client == null) {
            return "✗ Client not found";
        }

        if (clientRepo.delete(id)) {
            // Log activity
            activityLog.log(userId, "DELETE_CLIENT",
                    String.format("%s удалил(а) клиента %s [ID:%d]", getUsernameById(userId), client.getName(), id));
            return "✓ Client deleted";
        }
        return "✗ Error deleting client";
    }

    @Override
    public String getFullDetails(int clientId) {
        return clientRepo.getFullClientDetails(clientId);
    }
}
