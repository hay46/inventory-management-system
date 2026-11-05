package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.domain.StockTransaction;
import com.injibara.iams.backend.dto.StockTransactionRequest;
import com.injibara.iams.backend.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/receive")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INVENTORY_MANAGER','STOREKEEPER')")
    public ResponseEntity<StockTransaction> receive(@RequestBody StockTransactionRequest req, Authentication auth) {
        return ResponseEntity.ok(stockService.receive(req, auth.getName()));
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INVENTORY_MANAGER','STOREKEEPER')")
    public ResponseEntity<StockTransaction> issue(@RequestBody StockTransactionRequest req, Authentication auth) {
        return ResponseEntity.ok(stockService.issue(req, auth.getName()));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INVENTORY_MANAGER')")
    public ResponseEntity<StockTransaction> adjust(@RequestBody Map<String, Object> body, Authentication auth) {
        Long itemId = Long.valueOf(body.get("itemId").toString());
        Integer delta = Integer.valueOf(body.get("delta").toString());
        String reason = body.get("reason").toString();
        return ResponseEntity.ok(stockService.adjust(itemId, delta, reason, auth.getName()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<StockTransaction>> history() {
        return ResponseEntity.ok(stockService.history());
    }

    @GetMapping("/history/{itemId}")
    public ResponseEntity<List<StockTransaction>> historyForItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(stockService.historyForItem(itemId));
    }
}
