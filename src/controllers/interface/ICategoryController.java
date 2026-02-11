package controllers.interfaces;

import java.util.List;

public interface ICategoryController {
    List<Category> getAll();
    Category getById(int id);
    boolean addCategory(String name);
    boolean updateCategory(int id, String name);
    boolean deleteCategory(int id);
}
