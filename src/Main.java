import ui.MyApplication;
import logic.ClientRepository;
import logic.IClientRepository;

public class Main {
    public static void main(String[] args) {
        IClientRepository repo = new ClientRepository();
        MyApplication app = new MyApplication(repo);
        app.start();
    }
}