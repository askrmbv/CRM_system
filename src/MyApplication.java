import logic.ClientRepository;
import models.Client;
import java.util.Scanner;

public class MyApplication {
    private ClientRepository repo = new ClientRepository();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n===== CRM SYSTEM PRO =====");
            System.out.println("1. Create Client | 2. Filter by Stage | 3. Show All | 4. Delete | 0. Exit");
            String choice = scanner.next();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1" -> addClientProcess();
                case "2" -> {
                    System.out.println("Stage: 1.Lid 2.Negotiation 3.Decision 4.Deal");
                    repo.findByStage(getStageByChoice(scanner.nextInt()));
                }
                case "3" -> repo.findByStage("ALL");
                case "4" -> {
                    System.out.print("ID: ");
                    System.out.println(repo.delete(scanner.nextInt()) ? "Deleted." : "Not found.");
                }
            }
        }
    }

    private void addClientProcess() {
        // Проверка имени СРАЗУ
        System.out.print("Name: ");
        String name = scanner.next();
        if (repo.isTaken("name", name)) {
            System.out.println("!!! ERROR: Name '" + name + "' already exists! Aborting.");
            return;
        }

        // Проверка Email СРАЗУ
        System.out.print("Email: ");
        String email = scanner.next();
        if (repo.isTaken("email", email)) {
            System.out.println("!!! ERROR: Email '" + email + "' is taken! Aborting.");
            return;
        }

        System.out.println("Select Stage: 1.Lid 2.Negotiation 3.Decision 4.Deal");
        String stage = getStageByChoice(scanner.nextInt());

        double price = 0;
        if (stage.equals("deal")) {
            System.out.print("Deal amount ($): ");
            price = scanner.nextDouble();
        }

        Client c = new Client(0, name, email, stage, price);
        if (repo.save(c)) {
            System.out.println("Saved! Assigned Rank: " + c.getPrivilege());
        }
    }

    private String getStageByChoice(int c) {
        return switch(c) {
            case 2 -> "negotiation";
            case 3 -> "decision";
            case 4 -> "deal";
            default -> "lid";
        };
    }
}