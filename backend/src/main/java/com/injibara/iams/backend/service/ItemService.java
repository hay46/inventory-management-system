package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Category;
import com.injibara.iams.backend.domain.Item;
import com.injibara.iams.backend.domain.Location;
import com.injibara.iams.backend.dto.ItemRequest;
import com.injibara.iams.backend.dto.ItemResponse;
import com.injibara.iams.backend.repository.CategoryRepository;
import com.injibara.iams.backend.repository.ItemRepository;
import com.injibara.iams.backend.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository,
                       LocationRepository locationRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
    }

    public List<ItemResponse> search(String q, Long categoryId, Long locationId) {
        return itemRepository.search(q, categoryId, locationId).stream().map(this::toResponse).toList();
    }

    public ItemResponse getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return toResponse(item);
    }

    @Transactional
    public ItemResponse create(ItemRequest req) {
        if (itemRepository.existsByItemCode(req.getItemCode())) {
            throw new IllegalArgumentException("Item code already exists");
        }
        Category category = req.getCategoryId() == null ? null :
                categoryRepository.findById(req.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Location location = req.getLocationId() == null ? null :
                locationRepository.findById(req.getLocationId())
                        .orElseThrow(() -> new IllegalArgumentException("Location not found"));
        Item item = Item.builder()
                .itemCode(req.getItemCode())
                .itemName(req.getItemName())
                .description(req.getDescription())
                .category(category)
                .location(location)
                .unit(req.getUnit() == null ? "Piece" : req.getUnit())
                .quantity(req.getQuantity() == null ? 0 : req.getQuantity())
                .minimumStock(req.getMinimumStock() == null ? 0 : req.getMinimumStock())
                .build();
        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse update(Long id, ItemRequest req) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!item.getItemCode().equals(req.getItemCode()) && itemRepository.existsByItemCode(req.getItemCode())) {
            throw new IllegalArgumentException("Item code already exists");
        }
        Category category = req.getCategoryId() == null ? null :
                categoryRepository.findById(req.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Location location = req.getLocationId() == null ? null :
                locationRepository.findById(req.getLocationId())
                        .orElseThrow(() -> new IllegalArgumentException("Location not found"));
        item.setItemCode(req.getItemCode());
        item.setItemName(req.getItemName());
        item.setDescription(req.getDescription());
        item.setCategory(category);
        item.setLocation(location);
        if (req.getUnit() != null) item.setUnit(req.getUnit());
        if (req.getQuantity() != null) item.setQuantity(req.getQuantity());
        if (req.getMinimumStock() != null) item.setMinimumStock(req.getMinimumStock());
        item.setUpdatedAt(java.time.LocalDateTime.now());
        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item not found");
        }
        itemRepository.deleteById(id);
    }

    public List<ItemResponse> lowStock() {
        return itemRepository.findAll().stream()
                .filter(i -> i.getQuantity() <= i.getMinimumStock())
                .map(this::toResponse).toList();
    }

    private ItemResponse toResponse(Item i) {
        return ItemResponse.builder()
                .id(i.getId())
                .itemCode(i.getItemCode())
                .itemName(i.getItemName())
                .description(i.getDescription())
                .categoryId(i.getCategory() == null ? null : i.getCategory().getId())
                .categoryName(i.getCategory() == null ? null : i.getCategory().getName())
                .locationId(i.getLocation() == null ? null : i.getLocation().getId())
                .locationName(i.getLocation() == null ? null : i.getLocation().getName())
                .unit(i.getUnit())
                .quantity(i.getQuantity())
                .minimumStock(i.getMinimumStock())
                .status(i.getStatus())
                .build();
    }
}
