package models;

public class Task {
    private int id;
    private String name;
    private int customerId;  // Link to customer
    private int categoryId;
    private String categoryName;  // For display
    private String customerName;  // For display

    public Task(int id, String name, int customerId, int categoryId) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
        this.categoryId = categoryId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCustomerId() { return customerId; }
    public int getCategoryId() { return categoryId; }

    // Setters for JOIN data
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return String.format("Task ID:%-3d | %-25s | Client: %-15s [ID:%d] | Category: %s",
                id, name,
                (customerName != null ? customerName : "Unknown"),
                customerId,
                (categoryName != null ? categoryName : "Cat #" + categoryId));
    }
}
