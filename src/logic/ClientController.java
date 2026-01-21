package logic;

import models.Client;

public class ClientController {
    private ClientRepository repo = new ClientRepository();

    public String add(String n, String e, double p) {
        if (repo.existsByName(n)) return "Error: Name exists!";
        return repo.save(new Client(0, n, e, 1, p)) ? "Success" : "Fail";
    }

    public void showAll() {
        repo.findAll();
    }

    public String remove(int id) {
        return repo.delete(id) ? "Deleted" : "Not found";
    }
}