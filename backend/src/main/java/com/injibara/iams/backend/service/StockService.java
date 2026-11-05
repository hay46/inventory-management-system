package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.*;
import com.injibara.iams.backend.dto.StockTransactionRequest;
import com.injibara.iams.backend.repository.ItemRepository;
import com.injibara.iams.backend.repository.StockTransactionRepository;
import com.injibara.iams.backend.repository.SupplierRepository;
import com.injibara.iams.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final ItemRepository itemRepository;
    private final StockTransactionRepository txRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;

    public StockService(ItemRepository itemRepository, StockTransactionRepository txRepository,
                        UserRepository userRepository, SupplierRepository supplierRepository) {
        this.itemRepository = itemRepository;
        this.txRepository = txRepository;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public StockTransaction receive(StockTransactionRequest req, String username) {
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Supplier supplier = req.getSupplierId() == null ? null :
                supplierRepository.findById(req.getSupplierId()).orElse(null);

        item.setQuantity(item.getQuantity() + req.getQuantity());
        item.setUpdatedAt(java.time.LocalDateTime.now());
        itemRepository.save(item);

        StockTransaction tx = StockTransaction.builder()
                .item(item)
                .type(TransactionType.RECEIVE)
                .quantity(req.getQuantity())
                .referenceNumber(req.getReferenceNumber())
                .notes(req.getNotes())
                .supplier(supplier)
                .performedBy(user)
                .build();
        return txRepository.save(tx);
    }

    @Transactional
    public StockTransaction issue(StockTransactionRequest req, String username) {
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (item.getQuantity() < req.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock available");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        item.setQuantity(item.getQuantity() - req.getQuantity());
        item.setUpdatedAt(java.time.LocalDateTime.now());
        itemRepository.save(item);

        StockTransaction tx = StockTransaction.builder()
                .item(item)
                .type(TransactionType.ISSUE)
                .quantity(req.getQuantity())
                .referenceNumber(req.getReferenceNumber())
                .notes(req.getNotes())
                .performedBy(user)
                .build();
        return txRepository.save(tx);
    }

    @Transactional
    public StockTransaction adjust(Long itemId, Integer delta, String reason, String username) {
        if (delta == null || delta == 0) {
            throw new IllegalArgumentException("Adjustment quantity cannot be zero");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Adjustment reason is required");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        int newQty = item.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Adjustment would result in negative stock");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        item.setQuantity(newQty);
        item.setUpdatedAt(java.time.LocalDateTime.now());
        itemRepository.save(item);

        StockTransaction tx = StockTransaction.builder()
                .item(item)
                .type(TransactionType.ADJUSTMENT)
                .quantity(delta)
                .notes(reason)
                .performedBy(user)
                .build();
        return txRepository.save(tx);
    }

    public List<StockTransaction> history() {
        return txRepository.findAllByOrderByTransactionDateDesc();
    }

    public List<StockTransaction> historyForItem(Long itemId) {
        return txRepository.findByItem_IdOrderByTransactionDateDesc(itemId);
    }
}
