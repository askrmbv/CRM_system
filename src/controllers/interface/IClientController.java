package controllers.interfaces;

import models.Client;
import models.Role;
import java.util.List;

public interface IClientController {
    String addClient(String name, String email, int stage, double price, String note, int userId, Role role);
    List<Client> getAll();
    String updateClient(int id, String name, String email, int stage, double price, String note, Role role);
    String deleteClient(int id, int userId, Role role);
    String getFullDetails(int clientId);
}
