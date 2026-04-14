package com.recyclewise.controller;

import com.recyclewise.service.TipService;
import com.recyclewise.service.WasteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the home page route.
 *
 * SOLID — (D) Dependency Inversion: depends on WasteItemService + TipService interfaces,
 *         NOT on concrete implementations directly.
 * SOLID — (S) Single Responsibility: only renders the home page.
 * SOLID — (I) Interface Segregation: only injects interfaces for methods it actually calls.
 * OOP   — Abstraction: zero knowledge of database, JPA, or H2.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final WasteItemService wasteItemService; // depends on abstraction
    private final TipService tipService;             // depends on abstraction

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("topTips", tipService.getTopTips());
        model.addAttribute("stats", wasteItemService.getWasteStatsByCategory());
        model.addAttribute("activePage", "home");
        return "pages/home";
    }
}
