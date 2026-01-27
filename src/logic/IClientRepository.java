package logic;

import models.Client;
import java.util.List;

public interface IClientRepository {
    // Добавь эти методы:
    List<Client> getAllClients();
    boolean addClient(Client client);

    // Если у вас будут еще методы (удаление, поиск), они тоже пишутся тут
    boolean deleteClient(int id);
}