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
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final com.injibara.iams.backend.repository.ItemRepository itemRepository;

    public ItemController(ItemService itemService, com.injibara.iams.backend.repository.ItemRepository itemRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
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

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INVENTORY_MANAGER')")
    public ResponseEntity<Map<String, Object>> importCsv(@RequestParam("file") MultipartFile file) {
        int created = 0, skipped = 0, errors = 0;
        List<String> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",", -1);
                // expected: itemCode,itemName,categoryName,locationName,unit,quantity,minimumStock
                if (cols.length < 2) { errors++; messages.add("Bad row: " + line); continue; }
                try {
                    ItemRequest req = new ItemRequest();
                    req.setItemCode(cols[0].trim());
                    req.setItemName(cols[1].trim());
                    if (cols.length > 2 && !cols[2].trim().isEmpty()) {
                        // find category by name (create if missing)
                    }
                    req.setUnit(cols.length > 2 && !cols[2].trim().isEmpty() ? cols[2].trim() : "Piece");
                    req.setQuantity(cols.length > 3 && !cols[3].trim().isEmpty() ? Integer.parseInt(cols[3].trim()) : 0);
                    req.setMinimumStock(cols.length > 4 && !cols[4].trim().isEmpty() ? Integer.parseInt(cols[4].trim()) : 0);
                    if (itemRepository.existsByItemCode(req.getItemCode())) { skipped++; continue; }
                    itemService.create(req);
                    created++;
                } catch (Exception e) {
                    errors++;
                    messages.add("Row error: " + line + " -> " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to read file: " + e.getMessage()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("created", created);
        result.put("skipped", skipped);
        result.put("errors", errors);
        result.put("messages", messages);
        return ResponseEntity.ok(result);
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
