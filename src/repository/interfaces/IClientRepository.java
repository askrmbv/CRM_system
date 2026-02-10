package logic;

import models.Client;
import java.util.List;

// Interface for working with clients (Dependency Inversion Principle)
public interface IClientRepository {
    boolean save(Client c);
    List<Client> getAll();
    boolean update(int id, String name, String email, int stage, double price, int taskId);
    boolean delete(int id);

    // JOIN operation - get full client information
    String getFullClientDetails(int clientId);
}
