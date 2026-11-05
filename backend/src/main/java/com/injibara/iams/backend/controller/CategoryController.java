package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.domain.Category;
import com.injibara.iams.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Category>> all() { return ResponseEntity.ok(service.all()); }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Category> create(@RequestBody Category c) { return ResponseEntity.ok(service.create(c)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category c) { return ResponseEntity.ok(service.update(id, c)); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
