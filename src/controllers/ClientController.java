package controllers;

import models.Role;
import exceptions.AccessDeniedException;
import exceptions.InvalidDataException;
import java.util.List;

// Controller for managing clients (Dependency Inversion - depends on interface)
public class ClientController {
    private final IClientRepository repo;

    public ClientController(IClientRepository repo) {
        this.repo = repo;
    }

    // Add client (Manager and Admin only)
    public String addClient(String name, String email, int stage, double price, int taskId, Role userRole) {
        // Role Management - access check
        if (userRole != Role.ADMIN && userRole != Role.MANAGER) {
            throw new AccessDeniedException("Only Admin or Manager can add clients");
        }

        // Validate input data
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new InvalidDataException("Invalid email format");
        }
        if (price < 0) {
            throw new InvalidDataException("Price cannot be negative");
        }

        // Create client with validated data
        Client client = new Client(0, name, email, stage, price, taskId);
        return repo.save(client) ? " ✓ Client added" : " ✗ Error saving";
    }

    // View all clients (all roles)
    public List<Client> getAll() {
        return repo.getAll();
    }

    // Lambda expression - filter clients by stage
    public void showByStage(int stage) {
        repo.getAll().stream()
                .filter(c -> c.getDealStage() == stage)
                .forEach(System.out::println);
    }

    // Update client (Manager and Admin only)
    public String updateClient(int id, String name, String email, int stage, double price, int taskId, Role userRole) {
        if (userRole != Role.ADMIN && userRole != Role.MANAGER) {
            throw new AccessDeniedException("Only Admin or Manager can update clients");
        }

        return repo.update(id, name, email, stage, price, taskId) ?
                " ✓ Updated" : " ✗ Not found";
    }

    // Delete client (Admin only!)
    public String deleteClient(int id, Role userRole) {
        if (userRole != Role.ADMIN) {
            throw new AccessDeniedException("Only Admin can delete clients");
        }

        return repo.delete(id) ? " ✓ Deleted" : " ✗ Error";
    }

    // JOIN operation - get full client information
    public String getFullDetails(int clientId) {
        return repo.getFullClientDetails(clientId);
    }
}
