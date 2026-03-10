package com.recyclewise.service.impl;

import com.recyclewise.exception.ResourceNotFoundException;
import com.recyclewise.model.WasteItem;
import com.recyclewise.repository.WasteItemRepository;
import com.recyclewise.service.WasteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete implementation of WasteItemService.
 *
 * OOP  — Encapsulation: repo is private; no outside class touches it directly
 * OOP  — Polymorphism: can be swapped for MockWasteItemServiceImpl in tests
 * SOLID — (S) Single Responsibility: handles ONLY waste item business logic
 * SOLID — (O) Open/Closed: extend by creating new implementations of WasteItemService
 * SOLID — (D) Dependency Inversion: depends on WasteItemRepository abstraction (JpaRepository)
 */
@Service
@RequiredArgsConstructor
public class WasteItemServiceImpl implements WasteItemService {

    private final WasteItemRepository wasteItemRepository;

    @Override
    public List<WasteItem> getAllWasteItems() {
        return wasteItemRepository.findAll();
    }

    @Override
    public List<WasteItem> getWasteItemsByCategory(String category) {
        return wasteItemRepository.findByCategory(category.toUpperCase());
    }

    @Override
    public List<WasteItem> searchWasteItems(String query) {
        if (query == null || query.isBlank()) {
            return getAllWasteItems();
        }
        return wasteItemRepository.searchByName(query.trim());
    }

    @Override
    public WasteItem getWasteItemById(Long id) {
        return wasteItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WasteItem", "id", id));
    }

    @Override
    public Map<String, Long> getWasteStatsByCategory() {
        return wasteItemRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(WasteItem::getCategory, Collectors.counting()));
    }
}
