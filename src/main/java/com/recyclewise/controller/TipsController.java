package com.recyclewise.controller;

import com.recyclewise.service.TipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/tips")
@RequiredArgsConstructor
public class TipsController {

    private final TipService tipService;

    private static final Set<String> VALID_CATEGORIES = Set.of(
        "RECYCLE", "COMPOST", "REDUCE", "REUSE"
    );

    @GetMapping
    public String tips(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isBlank()) {
            String sanitizedCategory = sanitizeCategory(category);
            if (VALID_CATEGORIES.contains(sanitizedCategory)) {
                model.addAttribute("tips", tipService.getTipsByCategory(sanitizedCategory));
                model.addAttribute("activeCategory", sanitizedCategory);
            } else {
                model.addAttribute("tips", tipService.getAllTips());
            }
        } else {
            model.addAttribute("tips", tipService.getAllTips());
        }
        model.addAttribute("activePage", "tips");
        return "pages/tips";
    }

    private String sanitizeCategory(String input) {
        if (input == null) return "";
        return input.replaceAll("[^A-Z_]", "").toUpperCase().trim();
    }
}
