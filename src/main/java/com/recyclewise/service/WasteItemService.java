package com.recyclewise.service;

import com.recyclewise.model.WasteItem;

import java.util.List;
import java.util.Map;

/**
 * Service interface for WasteItem operations.
 *
 * OOP  — Abstraction: exposes only what callers need to know
 * SOLID — (D) Dependency Inversion: controllers depend on this abstraction, not the concrete impl
 * SOLID — (I) Interface Segregation: split from TipService — callers only depend on what they use
 * SOLID — (O) Open/Closed: new item-type logic can be added via new implementations, not edits
 */
public interface WasteItemService {

    List<WasteItem> getAllWasteItems();

    List<WasteItem> getWasteItemsByCategory(String category);

    List<WasteItem> searchWasteItems(String query);

    WasteItem getWasteItemById(Long id);

    Map<String, Long> getWasteStatsByCategory();
}
