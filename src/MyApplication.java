import logic.ClientRepository;
import models.Client;
import java.util.Scanner;

public class MyApplication {
    private ClientRepository repo = new ClientRepository();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n--- CRM SYSTEM ---");
            System.out.println("1.Add | 2.Filter | 3.List | 4.Delete | 0.Exit");
            String choice = scanner.next();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1" -> addFlow();
                case "2" -> filterFlow();
                case "3" -> repo.findByStage("ALL");
                case "4" -> {
                    System.out.print("Enter ID: ");
                    System.out.println(repo.delete(scanner.nextInt()) ? "OK" : "Error");
                }
            }
        }
    }

    private void addFlow() {
        System.out.print("Name: "); String n = scanner.next();
        if (repo.isTaken("name", n)) { System.out.println("Name taken!"); return; }

        System.out.print("Email: "); String e = scanner.next();
        if (repo.isTaken("email", e)) { System.out.println("Email taken!"); return; }

        System.out.println("Stage: 1.Lid 2.Negotiation 3.Decision 4.Deal");
        String s = getStage(scanner.nextInt());

        double p = 0;
        if (s.equals("deal")) {
            System.out.print("Amount: "); p = scanner.nextDouble();
        }

        if (repo.save(new Client(0, n, e, s, p))) System.out.println("Saved");
    }

    private void filterFlow() {
        System.out.println("Select: 1.Lid 2.Negotiation 3.Decision 4.Deal");
        repo.findByStage(getStage(scanner.nextInt()));
    }

    private String getStage(int i) {
        return switch(i) {
            case 2 -> "negotiation";
            case 3 -> "decision";
            case 4 -> "deal";
            default -> "lid";
        };
    }
}
// asanali