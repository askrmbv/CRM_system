package repository.interfaces;

import models.User;

public interface IUserRepository {
    User findByUsername(String username);
    boolean updateLastLogin(int userId);
    String getUsernameById(int userId);
}
