package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Item;
import com.injibara.iams.backend.domain.User;
import com.injibara.iams.backend.dto.StockTransactionRequest;
import com.injibara.iams.backend.repository.ItemRepository;
import com.injibara.iams.backend.repository.StockTransactionRepository;
import com.injibara.iams.backend.repository.SupplierRepository;
import com.injibara.iams.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock private ItemRepository itemRepository;
    @Mock private StockTransactionRepository txRepository;
    @Mock private UserRepository userRepository;
    @Mock private SupplierRepository supplierRepository;

    @InjectMocks private StockService stockService;

    @Test
    void receive_withZeroQuantity_throws() {
        StockTransactionRequest req = new StockTransactionRequest();
        req.setItemId(1L);
        req.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> stockService.receive(req, "admin"));
    }

    @Test
    void issue_moreThanAvailable_throws() {
        Item item = new Item();
        item.setId(1L);
        item.setQuantity(5);
        item.setItemCode("X");
        item.setItemName("X");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        StockTransactionRequest req = new StockTransactionRequest();
        req.setItemId(1L);
        req.setQuantity(10);
        assertThrows(IllegalArgumentException.class, () -> stockService.issue(req, "admin"));
    }
}
