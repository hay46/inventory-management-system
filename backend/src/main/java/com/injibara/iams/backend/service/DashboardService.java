package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Item;
import com.injibara.iams.backend.domain.StockTransaction;
import com.injibara.iams.backend.repository.ItemRepository;
import com.injibara.iams.backend.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final ItemRepository itemRepository;
    private final StockTransactionRepository txRepository;

    public DashboardService(ItemRepository itemRepository, StockTransactionRepository txRepository) {
        this.itemRepository = itemRepository;
        this.txRepository = txRepository;
    }

    public Map<String, Object> summary() {
        List<Item> items = itemRepository.findAll();
        long totalItems = items.size();
        long totalQty = items.stream().mapToInt(Item::getQuantity).sum();
        long lowStock = items.stream().filter(i -> i.getQuantity() > 0 && i.getQuantity() <= i.getMinimumStock()).count();
        long outOfStock = items.stream().filter(i -> i.getQuantity() == 0).count();
        List<StockTransaction> txs = txRepository.findAll();
        long received = txs.stream().filter(t -> t.getType().name().equals("RECEIVE")).count();
        long issued = txs.stream().filter(t -> t.getType().name().equals("ISSUE")).count();
        Map<String, Object> m = new HashMap<>();
        m.put("totalItems", totalItems);
        m.put("totalQuantity", totalQty);
        m.put("lowStockCount", lowStock);
        m.put("outOfStockCount", outOfStock);
        m.put("totalReceiveTransactions", received);
        m.put("totalIssueTransactions", issued);
        m.put("recentTransactions", txs.stream().limit(10).toList());
        return m;
    }
}
