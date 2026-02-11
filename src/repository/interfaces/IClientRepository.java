package repository.interfaces;

import models.Client;
import java.util.List;

public interface IClientRepository {
    boolean save(Client c);
    List<Client> getAll();
    Client getById(int id);
    boolean update(int id, String name, String email, int stage, double price, String note);
    boolean delete(int id);
    String getFullClientDetails(int clientId);

    // Lambda methods
    List<Client> getClientsByMinPrice(double minPrice);
    List<Client> getClientsByStage(int stage);
    List<Client> getClientsSortedByPrice();
    double getTotalRevenue();
}
