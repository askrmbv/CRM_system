import logic.ClientController;
import java.util.Scanner;

public class MyApplication {
    private ClientController controller = new ClientController();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n===== CRM SYSTEM =====");
            System.out.println("1. Add Client | 2. Show All | 3. Delete | 0. Exit");
            try {
                int choice = scanner.nextInt();
                if (choice == 0) break;
                switch (choice) {
                    case 1 -> {
                        System.out.print("Name: "); String n = scanner.next();
                        System.out.print("Email: "); String e = scanner.next();
                        System.out.print("Price: "); double p = scanner.nextDouble();
                        System.out.println(controller.add(n, e, p));
                    }
                    case 2 -> controller.showAll();
                    case 3 -> {
                        System.out.print("Enter ID: ");
                        int id = scanner.nextInt();
                        System.out.println(controller.remove(id));
                    }
                }
            } catch (Exception e) {
                System.out.println("Input error!");
                scanner.nextLine();
            }
        }
    }
}
