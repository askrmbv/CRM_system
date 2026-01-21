package logic;
import models.Client;

public class ClientController {
    private ClientRepository repo = new ClientRepository();
}
public String register(String name, String email, int status, double price) {
    if (name == null || name.isEmpty()) return "Error: Name is empty!";
    if (repo.existsByName(name)) return "Error: Name '" + name + "' already exists!";
    if (!email.contains("@")) return "Error: Invalid Email!";

    Client c = new Client(0, name, email, status, price);
    return repo.save(c) ? "Client registered successfully!" : "Registration failed!";
}