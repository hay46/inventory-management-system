package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Item;
import com.injibara.iams.backend.dto.ItemRequest;
import com.injibara.iams.backend.repository.CategoryRepository;
import com.injibara.iams.backend.repository.ItemRepository;
import com.injibara.iams.backend.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock private ItemRepository itemRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private LocationRepository locationRepository;

    @InjectMocks private ItemService itemService;

    @Test
    void create_withDuplicateCode_throws() {
        ItemRequest req = new ItemRequest();
        req.setItemCode("DUP");
        req.setItemName("Test");
        when(itemRepository.existsByItemCode("DUP")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> itemService.create(req));
    }

    @Test
    void create_success_savesItem() {
        ItemRequest req = new ItemRequest();
        req.setItemCode("NEW");
        req.setItemName("New Item");
        req.setQuantity(5);
        req.setMinimumStock(2);
        when(itemRepository.existsByItemCode("NEW")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));
        var resp = itemService.create(req);
        assertEquals("NEW", resp.getItemCode());
        assertEquals(5, resp.getQuantity());
    }

    @Test
    void lowStock_filtersItemsBelowMinimum() {
        Item a = new Item();
        a.setItemCode("A"); a.setItemName("A"); a.setQuantity(2); a.setMinimumStock(5);
        Item b = new Item();
        b.setItemCode("B"); b.setItemName("B"); b.setQuantity(10); b.setMinimumStock(5);
        when(itemRepository.findAll()).thenReturn(List.of(a, b));
        var res = itemService.lowStock();
        assertEquals(1, res.size());
        assertEquals("A", res.get(0).getItemCode());
    }
}
