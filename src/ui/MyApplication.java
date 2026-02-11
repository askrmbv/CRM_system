package ui;

import controllers.*;
import repository.*;
import models.*;
import exceptions.*;
import java.util.Scanner;

public class MyApplication {
    private User currentUser;
    private final Scanner scanner = new Scanner(System.in);

    // Repositories
    private final UserRepository userRepo;
    private final ClientRepository clientRepo;
    private final TaskRepository taskRepo;
    private final CategoryRepository categoryRepo;
    private final ActivityLogRepository activityLogRepo;
    private final NoteRepository noteRepo;  // NEW!

    // Controllers
    private final ClientController clientCtrl;
    private final TaskController taskCtrl;
    private final CategoryController categoryCtrl;  // NEW!

    public MyApplication() {
        // Initialize repositories
        this.userRepo = new UserRepository();
        this.clientRepo = new ClientRepository();
        this.categoryRepo = new CategoryRepository();
        this.activityLogRepo = new ActivityLogRepository();
        this.taskRepo = new TaskRepository();
        this.noteRepo = new NoteRepository();  // NEW!

        // Initialize controllers with userRepo
        this.clientCtrl = new ClientController(clientRepo, activityLogRepo, userRepo);
        this.taskCtrl = new TaskController(taskRepo, activityLogRepo, clientRepo, userRepo);
        this.categoryCtrl = new CategoryController(categoryRepo);  // NEW!
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

    // Authentication via database
    private boolean authenticate() {
        System.out.println("\n------------- LOGIN ------------");
        System.out.print("Username: ");
        String username = scanner.nextLine().toLowerCase();

        User user = userRepo.findByUsername(username);
        if (user == null) {
            System.out.println("User not found!");
            return false;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (user.getPassword().equals(password)) {
            this.currentUser = user;
            userRepo.updateLastLogin(user.getId());
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
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║                MAIN MENU                   ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("1. Dashboard");
            System.out.println("2. Clients");
            System.out.println("3. Tasks");
            System.out.println("4. Activity Log");
            System.out.println("5. Settings");
            System.out.println("0. Logout");
            System.out.print("\nChoice: ");

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
                case 4 -> activityLogMenu();
                case 5 -> settingsMenu();
                case 0 -> {
                    System.out.println("Goodbye, " + currentUser.getUsername() + ".");
                    return;
                }
                default -> System.out.println("[!] Invalid choice");
            }
        }
    }

    // Dashboard (БЕЗ recent activity)
    private void dashboardMenu() {
        System.out.println("\n═════════ DASHBOARD ═════════");
        System.out.println("Logged in as: " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        System.out.println("\nQuick Stats:");

        long clientCount = clientCtrl.getAll().size();
        System.out.println("Total Clients: " + clientCount);

        long taskCount = taskCtrl.getAll().size();
        System.out.println("Total Tasks: " + taskCount);

        // Show recent activity using getRecent()
        System.out.println("\nRecent Activity (Last 5):");
        var recentLogs = activityLogRepo.getRecent(5);  // ← USES getRecent()!
        if (recentLogs.isEmpty()) {
            System.out.println("[!] No recent activity");
        } else {
            recentLogs.forEach(log -> System.out.println("  • " + log));
        }

        pressEnterToContinue();
    }

    // Clients menu (БЕЗ view notes и add note)
    private void clientsMenu() {
        while (true) {
            System.out.println("\n═════════ CLIENTS MENU ═════════");
            System.out.println("1. List All Clients");
            System.out.println("2. Add Client");
            System.out.println("3. Update Client");
            System.out.println("4. Delete Client (Admin only)");
            System.out.println("5. View Client Details");
            System.out.println("6. Filter by Stage");
            System.out.println("7. Filter by Min Price");
            System.out.println("8. Sort by Price (High to Low)");
            System.out.println("9. Show Total Revenue");
            System.out.println("10. View/Add Notes");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) continue;
            int choice = Integer.parseInt(input);

            try {
                switch (choice) {
                    case 1 -> listAllClientsUI();
                    case 2 -> addClientUI();
                    case 3 -> updateClientUI();
                    case 4 -> deleteClientUI();
                    case 5 -> viewClientDetailsUI();
                    case 6 -> filterByStageUI();
                    case 7 -> filterByMinPriceUI();  // NEW: uses lambda
                    case 8 -> sortByPriceUI();       // NEW: uses lambda
                    case 9 -> showTotalRevenueUI();  // NEW: uses lambda
                    case 10 -> notesMenuUI();        // NEW: uses NoteRepository!
                    case 0 -> { return; }
                }
            } catch (AccessDeniedException e) {
                System.out.println("✗ Access Denied: " + e.getMessage());
            } catch (InvalidDataException e) {
                System.out.println("✗ Validation Error: " + e.getMessage());
            }
        }
    }

    private void listAllClientsUI() {
        System.out.println("\n═════════ ALL CLIENTS ═════════");
        var clients = clientCtrl.getAll();
        if (clients.isEmpty()) {
            System.out.println("[!] No clients yet. Add your first client!");
        } else {
            clients.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    private void addClientUI() {
        System.out.println("\n═════════ ADD CLIENT ═════════");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("Stage: 1 = Lid, 2 = Negotiation, 3 = Decision, 4 = Deal");
        System.out.print("Stage: ");
        int stage = Integer.parseInt(scanner.nextLine());

        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Note (press Enter to skip): ");
        String note = scanner.nextLine();

        String result = clientCtrl.addClient(name, email, stage, price, note, currentUser.getId(), currentUser.getRole());
        System.out.println(result);
        pressEnterToContinue();
    }

    private void updateClientUI() {
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients to update");
            pressEnterToContinue();
            return;
        }

        System.out.print("\nClient ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("New Name: ");
        String name = scanner.nextLine();

        System.out.print("New Email: ");
        String email = scanner.nextLine();

        System.out.print("New Stage (1-4): ");
        int stage = Integer.parseInt(scanner.nextLine());

        System.out.print("New Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("New Note (press Enter to keep current): ");
        String note = scanner.nextLine();

        String result = clientCtrl.updateClient(id, name, email, stage, price, note, currentUser.getRole());
        System.out.println(result);
        pressEnterToContinue();
    }

    private void deleteClientUI() {
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients to delete");
            pressEnterToContinue();
            return;
        }

        System.out.print("\nClient ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        String result = clientCtrl.deleteClient(id, currentUser.getId(), currentUser.getRole());
        System.out.println(result);
        pressEnterToContinue();
    }

    private void viewClientDetailsUI() {
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients available!");
            pressEnterToContinue();
            return;
        }

        System.out.print("\nClient ID: ");
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

        System.out.println("\nFilter by stage: 1 = Lid, 2 = Negotiation, 3 = Decision, 4 = Deal");
        System.out.print("Stage: ");
        int stage = Integer.parseInt(scanner.nextLine());

        System.out.println("\n═════════ FILTERED CLIENTS ═════════");
        var filtered = clientCtrl.getAll().stream()
                .filter(c -> c.getDealStage() == stage)
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("[!] No clients in this stage");
        } else {
            filtered.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    // Tasks menu
    private void tasksMenu() {
        while (true) {
            System.out.println("\n═════════ TASKS MENU ═════════");
            System.out.println("1. List All Tasks");
            System.out.println("2. Add Task");
            System.out.println("3. View Tasks by Client");
            System.out.println("4. Update Task");
            System.out.println("5. Delete Task (Admin only)");
            System.out.println("6. Filter by Category");
            System.out.println("7. Count Tasks for Client");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) continue;
            int choice = Integer.parseInt(input);

            try {
                switch (choice) {
                    case 1 -> listAllTasksUI();
                    case 2 -> addTaskUI();
                    case 3 -> viewTasksByClientUI();
                    case 4 -> updateTaskUI();
                    case 5 -> deleteTaskUI();
                    case 6 -> filterTasksByCategoryUI();  // NEW: uses lambda
                    case 7 -> countTasksByClientUI();     // NEW: uses lambda
                    case 0 -> { return; }
                    default -> System.out.println("[!] Invalid choice!");
                }
            } catch (AccessDeniedException e) {
                System.out.println("✗ Access Denied: " + e.getMessage());
            } catch (InvalidDataException e) {
                System.out.println("✗ Validation Error: " + e.getMessage());
            }
        }
    }

    private void listAllTasksUI() {
        System.out.println("\n═════════ ALL TASKS ═════════");
        var tasks = taskCtrl.getAll();
        if (tasks.isEmpty()) {
            System.out.println("[!] No tasks yet. Add your first task");
        } else {
            tasks.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    private void addTaskUI() {
        System.out.println("\n═════════ ADD TASK ═════════");

        // Check if clients exist first
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients found!");
            System.out.println("Please create a Client first (Go to Clients Menu > Add Client)");
            pressEnterToContinue();
            return;
        }

        // Check if categories exist
        if (categoryRepo.getAll().isEmpty()) {
            System.out.println("[!] No categories found!");
            System.out.println("Please add categories to the database first.");
            pressEnterToContinue();
            return;
        }

        System.out.print("Task Name: ");
        String name = scanner.nextLine();

        System.out.println("\nAvailable Clients:");
        clientCtrl.getAll().forEach(System.out::println);
        System.out.print("Client ID: ");
        int clientId = Integer.parseInt(scanner.nextLine());

        System.out.println("\nAvailable Categories:");
        categoryRepo.getAll().forEach(System.out::println);
        System.out.print("Category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        String result = taskCtrl.addTask(name, clientId, categoryId, currentUser.getId(), currentUser.getRole());
        System.out.println(result);
        pressEnterToContinue();
    }

    private void viewTasksByClientUI() {
        if (clientCtrl.getAll().isEmpty()) {
            System.out.println("[!] No clients available!");
            pressEnterToContinue();
            return;
        }

        System.out.print("\nClient ID: ");
        int clientId = Integer.parseInt(scanner.nextLine());

        System.out.println("\n═════════ TASKS FOR CLIENT ═════════");
        var tasks = taskCtrl.getByClientId(clientId);
        if (tasks.isEmpty()) {
            System.out.println("[!] No tasks for this client");
        } else {
            tasks.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    private void updateTaskUI() {
        if (taskCtrl.getAll().isEmpty()) {
            System.out.println("[!] No tasks to update!");
            pressEnterToContinue();
            return;
        }

        System.out.print("\nTask ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("New Name: ");
        String name = scanner.nextLine();

        System.out.println("\nAvailable Categories:");
        categoryRepo.getAll().forEach(System.out::println);
        System.out.print("New Category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        boolean success = taskCtrl.updateTask(id, name, categoryId);
        System.out.println(success ? "✓ Task updated!" : "✗ Task not found");
        pressEnterToContinue();
    }

    private void deleteTaskUI() {
        if (taskCtrl.getAll().isEmpty()) {
            System.out.println("[!] No tasks to delete");
            pressEnterToContinue();
            return;
        }

        System.out.print("\nTask ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        String result = taskCtrl.deleteTask(id, currentUser.getId(), currentUser.getRole());
        System.out.println(result);
        pressEnterToContinue();
    }

    // Activity Log menu
    private void activityLogMenu() {
        while (true) {
            System.out.println("\n═════════ ACTIVITY LOG MENU ═════════");
            System.out.println("1. Show All Logs");
            System.out.println("2. Filter by Action Type");
            System.out.println("3. Filter by User");
            System.out.println("4. Show Action Statistics");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            
            String input = scanner.nextLine();
            if (!input.matches("\\d+")) continue;
            int choice = Integer.parseInt(input);
            
            switch (choice) {
                case 1 -> showAllLogsUI();
                case 2 -> filterLogsByActionTypeUI();  // NEW: uses lambda
                case 3 -> filterLogsByUserUI();        // NEW: uses lambda
                case 4 -> showActionStatisticsUI();    // NEW: uses lambda
                case 0 -> { return; }
            }
        }
    }

    // NEW: Show all logs
    private void showAllLogsUI() {
        System.out.println("\n═════════ ALL ACTIVITY LOGS ═════════");
        var logs = activityLogRepo.getAll();
        if (logs.isEmpty()) {
            System.out.println("[!] No activity recorded yet");
        } else {
            logs.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    // NEW: Filter logs by action type (uses lambda)
    private void filterLogsByActionTypeUI() {
        System.out.println("\n═════════ AVAILABLE ACTION TYPES ═════════");
        var actionTypes = activityLogRepo.getAllActionTypes();  // ← LAMBDA METHOD!
        
        if (actionTypes.isEmpty()) {
            System.out.println("[!] No logs yet");
            pressEnterToContinue();
            return;
        }
        
        actionTypes.forEach(type -> System.out.println("• " + type));
        
        System.out.print("\nEnter action type: ");
        String actionType = scanner.nextLine().toUpperCase();
        
        var logs = activityLogRepo.getLogsByActionType(actionType);  // ← LAMBDA METHOD!
        
        System.out.println("\n═════════ LOGS: " + actionType + " ═════════");
        if (logs.isEmpty()) {
            System.out.println("[!] No logs found for this action type");
        } else {
            logs.forEach(System.out::println);
            System.out.println("\nTotal: " + logs.size() + " logs");
        }
        pressEnterToContinue();
    }

    // NEW: Filter logs by user (uses lambda)
    private void filterLogsByUserUI() {
        System.out.print("Enter user ID: ");
        try {
            int userId = Integer.parseInt(scanner.nextLine());
            var logs = activityLogRepo.getLogsByUserId(userId);  // ← LAMBDA METHOD!
            
            System.out.println("\n═════════ LOGS FOR USER #" + userId + " ═════════");
            if (logs.isEmpty()) {
                System.out.println("[!] No logs found for this user");
            } else {
                logs.forEach(System.out::println);
                System.out.println("\nTotal: " + logs.size() + " actions");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid user ID!");
        }
        pressEnterToContinue();
    }

    // NEW: Show action statistics (uses lambda)
    private void showActionStatisticsUI() {
        System.out.println("\n═════════ ACTION STATISTICS ═════════");
        var actionTypes = activityLogRepo.getAllActionTypes();  // ← LAMBDA METHOD!
        
        if (actionTypes.isEmpty()) {
            System.out.println("[!] No activity recorded yet");
        } else {
            System.out.println("Action Type | Count");
            System.out.println("─────────────────────");
            for (String type : actionTypes) {
                long count = activityLogRepo.countActionsByType(type);  // ← LAMBDA METHOD!
                System.out.printf("%-15s | %d\n", type, count);
            }
        }
        pressEnterToContinue();
    }

    // Settings
    private void settingsMenu() {
        while (true) {
            System.out.println("\n═════════ SETTINGS MENU ═════════");
            System.out.println("Current User: " + currentUser);
            System.out.println("\n1. View All Categories");
            System.out.println("2. Add Category");
            System.out.println("3. Update Category");
            System.out.println("4. Delete Category");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            if (!input.matches("\\d+")) continue;
            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1 -> viewAllCategoriesUI();
                case 2 -> addCategoryUI();
                case 3 -> updateCategoryUI();
                case 4 -> deleteCategoryUI();
                case 0 -> { return; }
            }
        }
    }

    private void viewAllCategoriesUI() {
        System.out.println("\n═════════ ALL CATEGORIES ═════════");
        var categories = categoryCtrl.getAll();  // ← USES CategoryController!
        if (categories.isEmpty()) {
            System.out.println("[!] No categories found");
        } else {
            categories.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    private void addCategoryUI() {
        System.out.println("\n═════════ ADD CATEGORY ═════════");
        System.out.print("Enter category name: ");
        String name = scanner.nextLine();

        if (categoryCtrl.addCategory(name)) {  // ← USES addCategory()!
            System.out.println("✓ Category added successfully!");
            activityLogRepo.log(currentUser.getId(), "ADD_CATEGORY",
                String.format("%s добавил(а) категорию %s", currentUser.getUsername(), name));
        } else {
            System.out.println("✗ Failed to add category (already exists or invalid)");
        }
        pressEnterToContinue();
    }

    private void updateCategoryUI() {
        System.out.println("\n═════════ UPDATE CATEGORY ═════════");
        var categories = categoryCtrl.getAll();
        if (categories.isEmpty()) {
            System.out.println("[!] No categories found");
            pressEnterToContinue();
            return;
        }

        categories.forEach(System.out::println);

        System.out.print("\nEnter category ID to update: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());

            Category cat = categoryCtrl.getById(id);  // ← USES getById()!
            if (cat == null) {
                System.out.println("✗ Category not found!");
                pressEnterToContinue();
                return;
            }

            System.out.println("Current name: " + cat.getName());
            System.out.print("Enter new name: ");
            String newName = scanner.nextLine();

            if (categoryCtrl.updateCategory(id, newName)) {  // ← USES updateCategory()!
                System.out.println("✓ Category updated!");
                activityLogRepo.log(currentUser.getId(), "UPDATE_CATEGORY",
                    String.format("%s обновил(а) категорию #%d", currentUser.getUsername(), id));
            } else {
                System.out.println("✗ Failed to update category");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID!");
        }
        pressEnterToContinue();
    }

    private void deleteCategoryUI() {
        System.out.println("\n═════════ DELETE CATEGORY ═════════");
        var categories = categoryCtrl.getAll();
        if (categories.isEmpty()) {
            System.out.println("[!] No categories found");
            pressEnterToContinue();
            return;
        }

        categories.forEach(System.out::println);

        System.out.print("\nEnter category ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());

            if (categoryCtrl.deleteCategory(id)) {  // ← USES deleteCategory()!
                System.out.println("✓ Category deleted!");
                activityLogRepo.log(currentUser.getId(), "DELETE_CATEGORY",
                    String.format("%s удалил(а) категорию #%d", currentUser.getUsername(), id));
            } else {
                System.out.println("✗ Failed to delete category");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID!");
        }
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("\n[!] Press Enter to continue...");
        scanner.nextLine();
    }

    // NEW: Filter clients by minimum price (uses lambda in ClientRepository)
    private void filterByMinPriceUI() {
        System.out.print("Enter minimum price: ");
        try {
            double minPrice = Double.parseDouble(scanner.nextLine());
            var clients = clientRepo.getClientsByMinPrice(minPrice);  // ← LAMBDA METHOD!
            
            System.out.println("\n═════════ CLIENTS WITH PRICE >= $" + minPrice + " ═════════");
            if (clients.isEmpty()) {
                System.out.println("[!] No clients found with price >= $" + minPrice);
            } else {
                clients.forEach(System.out::println);
                System.out.println("\nTotal found: " + clients.size());
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid price format!");
        }
        pressEnterToContinue();
    }

    // NEW: Sort clients by price descending (uses lambda in ClientRepository)
    private void sortByPriceUI() {
        var clients = clientRepo.getClientsSortedByPrice();  // ← LAMBDA METHOD!
        
        System.out.println("\n═════════ CLIENTS SORTED BY PRICE (HIGH → LOW) ═════════");
        if (clients.isEmpty()) {
            System.out.println("[!] No clients yet");
        } else {
            clients.forEach(System.out::println);
        }
        pressEnterToContinue();
    }

    // NEW: Show total revenue (uses lambda in ClientRepository)
    private void showTotalRevenueUI() {
        double totalRevenue = clientRepo.getTotalRevenue();  // ← LAMBDA METHOD!
        
        System.out.println("\n═════════ REVENUE STATISTICS ═════════");
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
        System.out.println("Total Clients: " + clientRepo.getAll().size());
        
        if (clientRepo.getAll().size() > 0) {
            double avgRevenue = totalRevenue / clientRepo.getAll().size();
            System.out.printf("Average per Client: $%.2f\n", avgRevenue);
        }
        
        pressEnterToContinue();
    }

    // NEW: Filter tasks by category (uses lambda in TaskRepository)
    private void filterTasksByCategoryUI() {
        System.out.println("\n═════════ AVAILABLE CATEGORIES ═════════");
        var categories = categoryRepo.getAll();
        if (categories.isEmpty()) {
            System.out.println("[!] No categories found");
            pressEnterToContinue();
            return;
        }
        
        categories.forEach(cat -> System.out.println(cat.getId() + ". " + cat.getName()));
        
        System.out.print("\nEnter category ID: ");
        try {
            int categoryId = Integer.parseInt(scanner.nextLine());
            var tasks = taskRepo.getTasksByCategory(categoryId);  // ← LAMBDA METHOD!
            
            System.out.println("\n═════════ TASKS IN CATEGORY #" + categoryId + " ═════════");
            if (tasks.isEmpty()) {
                System.out.println("[!] No tasks found in this category");
            } else {
                tasks.forEach(System.out::println);
                System.out.println("\nTotal: " + tasks.size() + " tasks");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID!");
        }
        pressEnterToContinue();
    }

    // NEW: Count tasks by client (uses lambda in TaskRepository)
    private void countTasksByClientUI() {
        System.out.println("\n═════════ ALL CLIENTS ═════════");
        var clients = clientRepo.getAll();
        if (clients.isEmpty()) {
            System.out.println("[!] No clients found");
            pressEnterToContinue();
            return;
        }
        
        clients.forEach(System.out::println);
        
        System.out.print("\nEnter client ID: ");
        try {
            int clientId = Integer.parseInt(scanner.nextLine());
            
            // Check if customer has tasks using lambda
            boolean hasTasks = taskRepo.customerHasTasks(clientId);  // ← LAMBDA METHOD!
            long taskCount = taskRepo.countTasksByCustomer(clientId);  // ← LAMBDA METHOD!
            
            System.out.println("\n═════════ TASK STATISTICS ═════════");
            System.out.println("Client ID: " + clientId);
            System.out.println("Has tasks: " + (hasTasks ? "YES" : "NO"));
            System.out.println("Total tasks: " + taskCount);
            
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID!");
        }
        pressEnterToContinue();
    }

    // NEW: Notes menu (uses NoteRepository!)
    private void notesMenuUI() {
        System.out.println("\n═════════ ALL CLIENTS ═════════");
        var clients = clientRepo.getAll();
        if (clients.isEmpty()) {
            System.out.println("[!] No clients found");
            pressEnterToContinue();
            return;
        }
        
        clients.forEach(System.out::println);
        
        System.out.print("\nEnter client ID: ");
        try {
            int clientId = Integer.parseInt(scanner.nextLine());
            
            // Check if client exists
            Client client = clientRepo.getById(clientId);
            if (client == null) {
                System.out.println("✗ Client not found!");
                pressEnterToContinue();
                return;
            }
            
            while (true) {
                System.out.println("\n═════════ NOTES FOR: " + client.getName() + " ═════════");
                System.out.println("1. View All Notes");
                System.out.println("2. Add New Note");
                System.out.println("3. Delete Note");
                System.out.println("0. Back");
                System.out.print("Choice: ");
                
                String input = scanner.nextLine();
                if (!input.matches("\\d+")) continue;
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1 -> {
                        var notes = noteRepo.getByCustomerId(clientId);  // ← USES NoteRepository!
                        if (notes.isEmpty()) {
                            System.out.println("[!] No notes for this client");
                        } else {
                            notes.forEach(System.out::println);
                        }
                        pressEnterToContinue();
                    }
                    case 2 -> {
                        System.out.print("Enter note text: ");
                        String noteText = scanner.nextLine();
                        
                        if (noteText.trim().isEmpty()) {
                            System.out.println("✗ Note cannot be empty!");
                        } else {
                            Note note = new Note(clientId, currentUser.getId(), noteText);
                            if (noteRepo.save(note)) {  // ← USES NoteRepository!
                                System.out.println("✓ Note added successfully!");
                                activityLogRepo.log(currentUser.getId(), "ADD_NOTE",
                                    String.format("%s добавил(а) заметку для %s", 
                                        currentUser.getUsername(), client.getName()));
                            } else {
                                System.out.println("✗ Failed to add note");
                            }
                        }
                        pressEnterToContinue();
                    }
                    case 3 -> {
                        var notes = noteRepo.getByCustomerId(clientId);
                        if (notes.isEmpty()) {
                            System.out.println("[!] No notes to delete");
                            pressEnterToContinue();
                            break;
                        }
                        
                        System.out.println("\n═════════ NOTES ═════════");
                        notes.forEach(note -> System.out.println("ID: " + note.getId() + " - " + note));
                        
                        System.out.print("\nEnter note ID to delete: ");
                        try {
                            int noteId = Integer.parseInt(scanner.nextLine());
                            if (noteRepo.delete(noteId)) {  // ← USES delete()!
                                System.out.println("✓ Note deleted successfully!");
                                activityLogRepo.log(currentUser.getId(), "DELETE_NOTE",
                                    String.format("%s удалил(а) заметку #%d", 
                                        currentUser.getUsername(), noteId));
                            } else {
                                System.out.println("✗ Failed to delete note");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("✗ Invalid note ID!");
                        }
                        pressEnterToContinue();
                    }
                    case 0 -> { return; }
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID!");
            pressEnterToContinue();
        }
    }

    public static void main(String[] args) {
        MyApplication app = new MyApplication();
        app.start();
    }
}
