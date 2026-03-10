package com.recyclewise.controller;

import com.recyclewise.service.PointsCalculator;
import com.recyclewise.service.StationService;
import com.recyclewise.service.SubmissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;
    private final SubmissionService submissionService;
    private final PointsCalculator pointsCalculator;

    @GetMapping
    public String stations(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        model.addAttribute("stations", stationService.getAllActiveStations());
        model.addAttribute("multipliers", pointsCalculator.getMultipliers());
        model.addAttribute("activePage", "stations");
        return "pages/stations";
    }

    @GetMapping("/{id}/submit")
    public String submitForm(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        model.addAttribute("station", stationService.findById(id));
        model.addAttribute("multipliers", pointsCalculator.getMultipliers());
        model.addAttribute("activePage", "stations");
        return "pages/submit-trash";
    }

    @PostMapping("/{id}/submit")
    public String submitTrash(@PathVariable Long id,
                              @RequestParam(defaultValue = "0") double recyclableKg,
                              @RequestParam(defaultValue = "0") double organicKg,
                              @RequestParam(defaultValue = "0") double hazardousKg,
                              @RequestParam(defaultValue = "0") double generalKg,
                              HttpSession session,
                              RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Map<String, Double> weights = new HashMap<>();
        weights.put("RECYCLABLE", recyclableKg);
        weights.put("ORGANIC", organicKg);
        weights.put("HAZARDOUS", hazardousKg);
        weights.put("GENERAL", generalKg);

        try {
            var submission = submissionService.submit(userId, id, weights);
            ra.addFlashAttribute("success",
                "✅ Submission recorded! You earned " + submission.getPointsEarned() + " points.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard";
    }
}
