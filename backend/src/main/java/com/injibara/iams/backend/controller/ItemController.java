package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.dto.ItemRequest;
import com.injibara.iams.backend.dto.ItemResponse;
import com.injibara.iams.backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<ItemResponse> items = itemService.search(null, null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("id,itemCode,itemName,category,location,unit,quantity,minimumStock,status\n");
        for (ItemResponse i : items) {
            sb.append(i.getId()).append(",")
              .append(escape(i.getItemCode())).append(",")
              .append(escape(i.getItemName())).append(",")
              .append(escape(i.getCategoryName())).append(",")
              .append(escape(i.getLocationName())).append(",")
              .append(escape(i.getUnit())).append(",")
              .append(i.getQuantity()).append(",")
              .append(i.getMinimumStock()).append(",")
              .append(i.getStatus()).append("\n");
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "inventory.csv");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
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
