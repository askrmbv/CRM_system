package ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyApplication {
    private String currentUser;
    private String userRole;
    private final Scanner scanner = new Scanner(System.in);

    // Login
    private final Map<String, String[]> users = new HashMap<>();

    public MyApplication() {
        // List check
        users.put("asanali", new String[]{"admin123", "Admin"});
        users.put("ayim", new String[]{"manager123", "Manager"});
        users.put("damir", new String[]{"editor123", "Editor"});
    }

    public void start() {
        if (authenticate()) {
            showMenu();
        }
    }

    private boolean authenticate() {
        System.out.println("------------- ENTER LOGIN AND PASSWORD ------------");

        System.out.print("Login: ");
        String login = scanner.nextLine().toLowerCase();

        if (!users.containsKey(login)) {
            System.out.println(" [!] Error: User not found!");
            return false;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        String[] userData = users.get(login);
        if (!userData[0].equals(password)) {
            System.out.println(" [!] Error: Incorrect password!");
            return false;
        }

        this.currentUser = login;
        this.userRole = userData[1];
        System.out.println("\nSuccessfully logged in! Welcome, " + currentUser + " (" + userRole + ")");
        return true;
    }

    private void showMenu() {
        while (true) {
            System.out.println("\n---------- CRM WORKPLACE ----------");
            System.out.println("User: " + currentUser + " | Role: " + userRole);
            System.out.println("1. Deals (Clients)");
            System.out.println("2. Tasks (Orders)");
            System.out.println("3. Contacts");
            System.out.println("4. Delete Operations (Danger Zone)");
            System.out.println("5. Settings");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            if (choice == 0) break;

            handleChoice(choice);
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> System.out.println("-> Accessing Deals section...");
            case 2 -> System.out.println("-> Accessing Tasks (JOIN table)...");
            case 3 -> System.out.println("-> Accessing Contacts...");
            case 4 -> {
                // Role Management
                if (!userRole.equals("Admin")) {
                    System.out.println(" [!] ACCESS DENIED: Only Admin can delete data.");
                } else {
                    System.out.println("-> Admin access granted. Proceeding to delete menu...");
                }
            }
            case 5 -> System.out.println("-> Opening Settings...");
            default -> System.out.println(" [!] Invalid choice.");
        }
    }
}