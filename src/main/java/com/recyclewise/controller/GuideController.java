package com.recyclewise.controller;

import com.recyclewise.service.WasteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all /guide routes.
 *
 * SOLID — (D) Dependency Inversion: depends only on WasteItemService interface.
 * SOLID — (S) Single Responsibility: renders waste guide views only.
 * SOLID — (I) Interface Segregation: does NOT inject TipService — it doesn't need tips.
 */
@Controller
@RequestMapping("/guide")
@RequiredArgsConstructor
public class GuideController {

    private final WasteItemService wasteItemService; // depends on abstraction

    @GetMapping
    public String guide(@RequestParam(required = false) String category,
                        @RequestParam(required = false) String search,
                        Model model) {

        if (search != null && !search.isBlank()) {
            model.addAttribute("items", wasteItemService.searchWasteItems(search));
            model.addAttribute("searchQuery", search);
        } else if (category != null && !category.isBlank()) {
            model.addAttribute("items", wasteItemService.getWasteItemsByCategory(category));
            model.addAttribute("activeCategory", category);
        } else {
            model.addAttribute("items", wasteItemService.getAllWasteItems());
        }

        model.addAttribute("activePage", "guide");
        return "pages/guide";
    }

    @GetMapping("/{id}")
    public String itemDetail(@PathVariable Long id, Model model) {
        // ResourceNotFoundException is thrown here if not found — caught by GlobalExceptionHandler
        model.addAttribute("item", wasteItemService.getWasteItemById(id));
        model.addAttribute("activePage", "guide");
        return "pages/item-detail";
    }
}
