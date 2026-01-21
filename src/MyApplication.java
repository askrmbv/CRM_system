package ui;
import logic.ClientController;
import java.util.Scanner;

public class MyApplication {
    private ClientController controller = new ClientController();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n===== CRM SYSTEM MENU =====");
            System.out.println("1. Add New Client");
            System.out.println("2. Show All Clients");
            System.out.println("3. Delete Client by ID");
            System.out.println("0. Exit");
            System.out.print("Select action: ");

            try {
                int choice = scanner.nextInt();
                if (choice == 0) break;

                switch (choice) {
                    case 1 -> {
                        System.out.print("Name: "); String n = scanner.next();
                        System.out.print("Email: "); String e = scanner.next();
                        System.out.print("Price: "); double p = scanner.nextDouble();
                        System.out.println("Result: " + controller.add(n, e, p));
                    }
                    case 2 -> controller.showAll();
                    case 3 -> {
                        System.out.print("Enter ID to delete: ");
                        int id = scanner.nextInt();
                        System.out.println("Result: " + controller.remove(id));
                    }
                    default -> System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Input error! Please use numbers.");
                scanner.nextLine(); // очистка буфера
            }
        }
    }
}
