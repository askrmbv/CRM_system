package logic;

import models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements IClientRepository {
    // ВАЖНО: Названия методов должны совпадать с IClientRepository 1 в 1

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        // Тут её старый код с Connection и ResultSet
        return clients;
    }

    @Override
    public boolean addClient(Client client) {
        String sql = "INSERT INTO clients(name, email, stage, price) VALUES(?,?,?,?)";
        // Тут её старый код с PreparedStatement
        return true;
    }

    @Override
    public boolean deleteClient(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        // Логика удаления
        return true;
    }
}