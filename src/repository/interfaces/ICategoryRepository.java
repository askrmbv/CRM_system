package repository.interfaces;

import java.util.List;

public interface ICategoryRepository {
    List<Category> getAll();
    Category getById(int id);
}
