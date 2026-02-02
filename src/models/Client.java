package models;

public class Client {
    private int id;
    private String name;
    private String email;
    private int dealStage;  // 1-4: Lid, Negotiation, Decision, Deal
    private double price;
    private int taskId;
    private String taskName; // For display after JOIN

    public Client(int id, String name, String email, int dealStage, double price, int taskId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dealStage = dealStage;
        this.price = price;
        this.taskId = taskId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getDealStage() { return dealStage; }
    public double getPrice() { return price; }
    public int getTaskId() { return taskId; }

    // Setter for JOIN data
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        String stage = switch(dealStage) {
            case 2 -> "Negotiation";
            case 3 -> "Decision";
            case 4 -> "Deal";
            default -> "Lid";
        };

        return String.format("ID:%-3d | %-15s | %-20s | Stage: %-12s | $%-8.2f | Task: %s",
                id, name, email, stage, price,
                (taskName != null ? taskName : "Task #" + taskId));
    }
}