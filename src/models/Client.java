package models;

public class Client {
    private int id;
    private String name;
    private String email;
    private String stage;
    private double price;
    private String privilege;

    public Client(int id, String name, String email, String stage, double price) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.stage = stage;
        this.price = price;
        this.privilege = autoRank(price);
    }

    private String autoRank(double p) {
        if (p <= 100) return "NONE";
        if (p <= 500) return "BRONZE";
        if (p <= 1000) return "SILVER";
        return "GOLD";
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getStage() { return stage; }
    public double getPrice() { return price; }
    public String getPrivilege() { return privilege; }
    public int getId() { return id; }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %-10s | Email: %-15s | Stage: %-11s | Price: %-7.2f | Privilege: [%s]",
                id, name, email, stage.toUpperCase(), price, privilege);
    }
}

// damir