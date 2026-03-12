package com.recyclewise.controller;

import com.recyclewise.model.User;
import com.recyclewise.service.RewardService;
import com.recyclewise.service.SubmissionService;
import com.recyclewise.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final SubmissionService submissionService;
    private final RewardService rewardService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId);

        // Auto-expire any vouchers past their expiry date
        rewardService.autoExpireRedemptions(user);

        // Only count points from CONFIRMED submissions
        model.addAttribute("user", user);
        model.addAttribute("submissions", submissionService.getSubmissionsForUser(user));
        model.addAttribute("redemptions", rewardService.getRedemptionsForUser(user));
        model.addAttribute("activePage", "dashboard");
        return "pages/dashboard";
    }
}
