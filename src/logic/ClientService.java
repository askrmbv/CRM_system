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
