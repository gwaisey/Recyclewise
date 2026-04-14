package com.recyclewise.controller;

import com.recyclewise.service.TipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all /tips routes.
 *
 * SOLID — (D) Dependency Inversion: depends only on TipService interface.
 * SOLID — (S) Single Responsibility: renders tips views only.
 * SOLID — (I) Interface Segregation: does NOT inject WasteItemService — it doesn't need it.
 */
@Controller
@RequestMapping("/tips")
@RequiredArgsConstructor
public class TipsController {

    private final TipService tipService; // depends on abstraction — not TipServiceImpl

    @GetMapping
    public String tips(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isBlank()) {
            model.addAttribute("tips", tipService.getTipsByCategory(category));
            model.addAttribute("activeCategory", category);
        } else {
            model.addAttribute("tips", tipService.getAllTips());
        }
        model.addAttribute("activePage", "tips");
        return "pages/tips";
    }
}
