package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Category;
import com.injibara.iams.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    public CategoryService(CategoryRepository repo) { this.repo = repo; }
    public List<Category> all() { return repo.findAll(); }
    public Category create(Category c) {
        if (repo.existsByName(c.getName())) throw new IllegalArgumentException("Category name already exists");
        return repo.save(c);
    }
    public Category update(Long id, Category c) {
        Category existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        existing.setName(c.getName());
        existing.setDescription(c.getDescription());
        return repo.save(existing);
    }
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Category not found");
        repo.deleteById(id);
    }
}
