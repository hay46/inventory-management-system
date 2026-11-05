package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.dto.ItemRequest;
import com.injibara.iams.backend.dto.ItemResponse;
import com.injibara.iams.backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long locationId) {
        return ResponseEntity.ok(itemService.search(q, categoryId, locationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ItemResponse>> lowStock() {
        return ResponseEntity.ok(itemService.lowStock());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INVENTORY_MANAGER')")
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest req) {
        return ResponseEntity.ok(itemService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INVENTORY_MANAGER')")
    public ResponseEntity<ItemResponse> update(@PathVariable Long id, @Valid @RequestBody ItemRequest req) {
        return ResponseEntity.ok(itemService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
