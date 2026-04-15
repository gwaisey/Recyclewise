package com.recyclewise.controller;

import com.recyclewise.service.WasteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/guide")
@RequiredArgsConstructor
public class GuideController {

    private final WasteItemService wasteItemService;

    private static final Set<String> VALID_CATEGORIES = Set.of(
        "RECYCLABLES", "ORGANIC", "HAZARDOUS", "GENERAL", "PAPER"
    );
    private static final int MAX_SEARCH_LENGTH = 100;

    @GetMapping
    public String guide(@RequestParam(required = false) String category,
                        @RequestParam(required = false) String search,
                        Model model) {

        String sanitizedSearch = sanitizeSearch(search);

        if (sanitizedSearch != null && !sanitizedSearch.isBlank()) {
            if (sanitizedSearch.length() > MAX_SEARCH_LENGTH) {
                sanitizedSearch = sanitizedSearch.substring(0, MAX_SEARCH_LENGTH);
            }
            model.addAttribute("items", wasteItemService.searchWasteItems(sanitizedSearch));
            model.addAttribute("searchQuery", sanitizedSearch);
        } else if (category != null && !category.isBlank()) {
            String sanitizedCategory = sanitizeCategory(category);
            if (VALID_CATEGORIES.contains(sanitizedCategory)) {
                model.addAttribute("items", wasteItemService.getWasteItemsByCategory(sanitizedCategory));
                model.addAttribute("activeCategory", sanitizedCategory);
            } else {
                model.addAttribute("items", wasteItemService.getAllWasteItems());
            }
        } else {
            model.addAttribute("items", wasteItemService.getAllWasteItems());
        }

        model.addAttribute("activePage", "guide");
        return "pages/guide";
    }

    @GetMapping("/{id}")
    public String itemDetail(@PathVariable Long id, Model model) {
        if (id == null || id <= 0) {
            return "redirect:/guide";
        }
        model.addAttribute("item", wasteItemService.getWasteItemById(id));
        model.addAttribute("activePage", "guide");
        return "pages/item-detail";
    }

    private String sanitizeSearch(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"']", "").trim();
    }

    private String sanitizeCategory(String input) {
        if (input == null) return "";
        return input.replaceAll("[^A-Z_]", "").toUpperCase().trim();
    }
}
