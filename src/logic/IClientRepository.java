package logic;

import models.Client;
import java.util.List;

public interface IClientRepository {
    boolean isTaken(String field, String value);
    boolean save(Client c);
    List<Client> findByStage(String stage);
    boolean delete(int id);
}