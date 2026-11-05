package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Category;
import com.injibara.iams.backend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository repo;
    @InjectMocks private CategoryService service;

    @Test
    void create_duplicateName_throws() {
        when(repo.existsByName("Dup")).thenReturn(true);
        Category c = new Category();
        c.setName("Dup");
        assertThrows(IllegalArgumentException.class, () -> service.create(c));
    }

    @Test
    void create_success() {
        when(repo.existsByName("New")).thenReturn(false);
        when(repo.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));
        Category c = new Category();
        c.setName("New");
        Category saved = service.create(c);
        assertEquals("New", saved.getName());
    }

    @Test
    void delete_missing_throws() {
        when(repo.existsById(99L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.delete(99L));
    }
}
