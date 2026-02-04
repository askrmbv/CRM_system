package ui;

import logic.*;
import models.*;
import exceptions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyApplication {
    private User currentUser;
    private final Scanner scanner = new Scanner(System.in);

    // Controllers
    private final ClientController clientCtrl;
    private final TaskController taskCtrl;
    private final CategoryRepository categoryRepo;

    // User storage
    private final Map<String, User> users = new HashMap<>();

    public MyApplication() {
        this.clientCtrl = new ClientController(new ClientRepository());
        this.taskCtrl = new TaskController(new TaskRepository());
        this.categoryRepo = new CategoryRepository();

        // Initialize users with Enum Role
        users.put("asanali", new User(1, "Asanali", "admin123", Role.ADMIN));
        users.put("ayim", new User(2, "Aiym", "manager123", Role.MANAGER));
        users.put("damir", new User(3, "Damir", "editor123", Role.EDITOR));
    }

    public void start() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║           CRM MANAGEMENT SYSTEM            ║");
        System.out.println("╚════════════════════════════════════════════╝");

        if (authenticate()) {
            mainMenu();
        } else {
            System.out.println("Login failed");
        }
    }

    // Authentication
    private boolean authenticate() {
        System.out.println("\n------------- LOGIN ------------");
        System.out.print("Username: ");
        String username = scanner.nextLine().toLowerCase();

        if (!users.containsKey(username)) {
            System.out.println("User not found!");
            return false;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user.getPassword().equals(password)) {
            this.currentUser = user;
            System.out.println("[!] Login confirmed. Welcome, " + currentUser.getUsername() +
                    " (Role: " + currentUser.getRole() + ")");
            return true;
        }

        System.out.println("[!] Wrong password");
        return false;
    }

    // Main menu
    private void mainMenu() {
        while (true) {
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║                MAIN MENU                   ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("1. Dashboard");
            System.out.println("2. Clients");
            System.out.println("3. Tasks");
            System.out.println("4. Settings");
            System.out.println("0. Logout");
            System.out.println();
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) {
                System.out.println("Invalid input!");
                continue;
            }

            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1 -> dashboardMenu();
                case 2 -> clientsMenu();
                case 3 -> tasksMenu();
                case 4 -> settingsMenu();
                case 0 -> {
                    System.out.println("Goodbye, " + currentUser.getUsername() + ".");
                    return;
                }
                default -> System.out.println("[!] Invalid choice");
            }
        }
    }

    // Dashboard
    private void dashboardMenu() {
        System.out.println("═════════ DASHBOARD ═════════");
        System.out.println("Logged in as: " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        System.out.println("\nQuick Stats:");

        // Lambda expression - count clients
        long clientCount = clientCtrl.getAll().size();
        System.out.println("Total Clients: " + clientCount);

        long taskCount = taskCtrl.getAll().size();
        System.out.println("Total Tasks: " + taskCount);

        pressEnterToContinue();
    }

    // Clients menu
    private void clientsMenu() {
        while (true) {
            System.out.println("═════════ CLIENTS MENU ═════════");
            System.out.println("1. List All Clients");
            System.out.println("2. Add Client");
            System.out.println("3. Update Client");
            System.out.println("4. Delete Client (Admin only)");
            System.out.println("5. View Client Details (JOIN)");
            System.out.println("6. Filter by Stage");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) continue;
            int choice = Integer.parseInt(input);

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.println("═════════ ALL CLIENTS ═════════");
                        var clients = clientCtrl.getAll();
                        if (clients.isEmpty()) {
                            System.out.println("[!] No clients yet. Add your first client!");
                        } else {
                            clients.forEach(System.out::println);
                        }
                    }
                    case 2 -> addClientUI();
                    case 3 -> updateClientUI();
                    case 4 -> deleteClientUI();
                    case 5 -> viewClientDetailsUI();
                    case 6 -> filterByStageUI();
                    case 0 -> { return; }
                }
            } catch (AccessDeniedException e) {
                System.out.println("✗ Access Denied: " + e.getMessage());
            } catch (InvalidDataException e) {
                System.out.println("✗ Validation Error: " + e.getMessage());
            }
        }
    }

    private void addClientUI() {
        System.out.println("\n═════════ ADD CLIENT ═════════");

        // Check if tasks exist first
        if (taskCtrl.getAll().isEmpty()) {
            System.out.println("[!] No tasks found");
            System.out.println("Please create a Task first (Go to Tasks Menu > Add Task)");
            pressEnterToContinue();
            return;
        }

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("Stage: 1 = Lid, 2 = Negotiation, 3 = Decision, 4 = Deal");
        System.out.print("Stage: ");
        int stage = Integer.parseInt(scanner.nextLine());

        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.println("\nAvailable Tasks:");
        taskCtrl.getAll().forEach(System.out::println);
        System.out.print("Task ID: ");
        int taskId = Integer.parseInt(scanner.nextLine());

        String result = clientCtrl.addClient(name, email, stage, price, taskId, currentUser.getRole());
        System.out.println(result);
    }

    private void updateClientUI() {
        // Check if clients exist
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients to update");
            pressEnterToContinue();
            return;
        }

        System.out.print("Client ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("New Name: ");
        String name = scanner.nextLine();

        System.out.print("New Email: ");
        String email = scanner.nextLine();

        System.out.print("New Stage (1-4): ");
        int stage = Integer.parseInt(scanner.nextLine());

        System.out.print("New Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("New Task ID: ");
        int taskId = Integer.parseInt(scanner.nextLine());

        String result = clientCtrl.updateClient(id, name, email, stage, price, taskId, currentUser.getRole());
        System.out.println(result);
    }

    private void deleteClientUI() {
        // Check if clients exist
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients to delete");
            pressEnterToContinue();
            return;
        }

        System.out.print("Client ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        String result = clientCtrl.deleteClient(id, currentUser.getRole());
        System.out.println(result);
    }

    // JOIN operation - detailed client information
    private void viewClientDetailsUI() {
        // Check if clients exist
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients available!");
            pressEnterToContinue();
            return;
        }

        System.out.print("Client ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        String details = clientCtrl.getFullDetails(id);
        System.out.println(details);
        pressEnterToContinue();
    }

    private void filterByStageUI() {
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients to filter");
            pressEnterToContinue();
            return;
        }

        System.out.println("Filter by stage: 1 = Lid, 2 = Negotiation, 3 = Decision, 4 = Deal");
        System.out.print("Stage: ");
        int stage = Integer.parseInt(scanner.nextLine());

        System.out.println("═════════ FILTERED CLIENTS ═════════");
        var filtered = clientCtrl.getAll().stream()
                .filter(c -> c.getDealStage() == stage)
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("[!] No clients in this stage");
        } else {
            filtered.forEach(System.out::println);
        }
    }


    // Tasks menu - fully functional
    private void tasksMenu() {
        while (true) {
            System.out.println("\n═════════ TASKS MENU ═════════");
            System.out.println("1. List All Tasks");
            System.out.println("2. Add Task");
            System.out.println("3. Update Task");
            System.out.println("4. Delete Task");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) continue;
            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1 -> {
                    System.out.println("\n═════════ ALL TASKS ═════════");
                    var tasks = taskCtrl.getAll();
                    if (tasks.isEmpty()) {
                        System.out.println("[!] No tasks yet. Add your first task");
                    } else {
                        tasks.forEach(System.out::println);
                    }
                    pressEnterToContinue();
                }
                case 2 -> addTaskUI();
                case 3 -> updateTaskUI();
                case 4 -> deleteTaskUI();
                case 0 -> { return; }
                default -> System.out.println("[!] Invalid choice!");
            }
        }
    }

    private void addTaskUI() {
        System.out.println("\n═════════ ADD TASK ═════════");

        // Check if categories exist
        if (categoryRepo.getAll().isEmpty()) {
            System.out.println("[!] No categories found!");
            System.out.println("Please add categories to the database first.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Task Name: ");
        String name = scanner.nextLine();

        System.out.println("\nAvailable Categories:");
        categoryRepo.getAll().forEach(System.out::println);
        System.out.print("Category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        boolean success = taskCtrl.addTask(name, categoryId);
        System.out.println(success ? "✓ Task added successfully!" : "✗ Error adding task");
    }

    private void updateTaskUI() {
        // Check if tasks exist
        if (taskCtrl.getAll().isEmpty()) {
            System.out.println("[!] No tasks to update!");
            pressEnterToContinue();
            return;
        }

        System.out.print("Task ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("New Name: ");
        String name = scanner.nextLine();

        System.out.println("\nAvailable Categories:");
        categoryRepo.getAll().forEach(System.out::println);
        System.out.print("New Category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        boolean success = taskCtrl.updateTask(id, name, categoryId);
        System.out.println(success ? "✓ Task updated!" : "✗ Task not found");
    }

    private void deleteTaskUI() {
        // Check if tasks exist
        if (taskCtrl.getAll().isEmpty()) {
            System.out.println("[!] No tasks to delete");
            pressEnterToContinue();
            return;
        }

        System.out.print("Task ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        boolean success = taskCtrl.deleteTask(id);
        System.out.println(success ? "[!] Task deleted" : "✗ Error deleting (maybe in use by clients)");
    }

    // Settings
    private void settingsMenu() {
        System.out.println("═════════ SETTINGS ═════════");
        System.out.println("Current User: " + currentUser);
        System.out.println("\nCategories:");
        var categories = categoryRepo.getAll();
        if (categories.isEmpty()) {
            System.out.println("[!] No categories found");
        } else {
            categories.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("[!] Press Enter to continue");
        scanner.nextLine();
    }
}