package logic;
import models.Client;

import java.util.List;

public class ClientController {
    private ClientRepository repo = new ClientRepository();

    public String add(String n, String e, String s, double p) {
        if (repo.isTaken("name", n)) return "Error: Name exists!";
        if (repo.isTaken("email", e)) return "Error: Email exists!";
        return repo.save(new Client(0, n, e, s, p)) ? "Success" : "Fail";
    }

    public List<Client> showAll() { return repo.findByStage("ALL"); }
    public List<Client> showByStage(String s) { return repo.findByStage(s); }
    public String remove(int id) { return repo.delete(id) ? "Deleted" : "Not found"; }
}

// ayim
