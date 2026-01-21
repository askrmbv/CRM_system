package logic;
import models.Client;

public class ClientController {
    private ClientRepository repo = new ClientRepository();

    public String add(String n, String e, String s, double p) {
        if (repo.isTaken("name", n)) return "Error: Name exists!";
        if (repo.isTaken("email", e)) return "Error: Email exists!";
        return repo.save(new Client(0, n, e, s, p)) ? "Success" : "Fail";
    }

    public void showAll() { repo.findByStage("ALL"); }
    public void showByStage(String s) { repo.findByStage(s); }
    public String remove(int id) { return repo.delete(id) ? "Deleted" : "Not found"; }
}

// ayim
