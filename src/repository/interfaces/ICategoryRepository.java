package repository.interfaces;

import models.Category;
import java.util.List;

public interface ICategoryRepository {
    List<Category> getAll();
    Category getById(int id);
}
