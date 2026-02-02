package models;

public class Task {
    private int id;
    private String name;
    private int categoryId;

    public Task(int id, String name, int categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getCategoryId() { return categoryId; }

    @Override
    public String toString() {
        return String.format("ID: %-3d | Name: %s", id, name);
    }
}
