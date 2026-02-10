package controllers;

public class CategoryController {
    private final ICategoryRepository categoryRepository;

    public CategoryController(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // some methods ...
}
