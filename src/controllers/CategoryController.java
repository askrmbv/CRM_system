package controllers;

import controllers.interfaces.ICategoryController;
import repository.interfaces.ICategoryRepository;
import models.Category;
import java.util.List;

public class CategoryController implements ICategoryController {
    private final ICategoryRepository categoryRepo;

    public CategoryController(ICategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepo.getAll();
    }

    @Override
    public Category getById(int id) {
        return categoryRepo.getById(id);
    }

    @Override
    public boolean addCategory(String name) {
        // Validation would go here if we had save method in repository
        return false; // Not implemented yet
    }

    @Override
    public boolean updateCategory(int id, String name) {
        // Update logic would go here
        return false; // Not implemented yet
    }

    @Override
    public boolean deleteCategory(int id) {
        // Delete logic would go here
        return false; // Not implemented yet
    }
}