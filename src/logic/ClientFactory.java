package logic;

import models.Client;

public class ClientFactory {
    // Creating Client
    public static Client createClient(String name, String email, String stage, double price) {
        return new Client(name, email, stage, price);
    }
}
