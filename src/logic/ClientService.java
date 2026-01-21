package logic;

public class ClientService {
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
public String getRank(double price) {
    if (price >= 1000) return "Gold";
    if (price >= 500) return "Silver";
    return "Standard";
}
public double applyDiscount(double price) {
    return (price > 2000) ? price * 0.95 : price;
}
