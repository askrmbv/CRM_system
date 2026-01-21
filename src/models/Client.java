package models;

public class Client {
    private int id;
    private String name;
    private String email;
    private int status;
    private double price;
    public Client(int id, String name, String email, int status, double price) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.price = price;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getStatus() { return status; }
    public double getPrice() { return price; }
}