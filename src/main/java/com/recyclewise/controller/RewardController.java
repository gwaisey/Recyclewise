package com.recyclewise.controller;

import com.recyclewise.model.User;
import com.recyclewise.service.RewardService;
import com.recyclewise.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final UserService userService;

    @GetMapping
    public String rewards(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        model.addAttribute("rewards", rewardService.getAllAvailableRewards());
        model.addAttribute("user", user);
        model.addAttribute("activePage", "rewards");
        return "pages/rewards";
    }

    @PostMapping("/{id}/mark-used")
    public String markUsed(@PathVariable Long id, HttpSession session,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            rewardService.markAsUsed(id, userId);
            ra.addFlashAttribute("success", "Voucher marked as used.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/redeem")
    public String redeem(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            var redemption = rewardService.redeem(userId, id);
            ra.addFlashAttribute("success",
                "🎉 Reward redeemed! Your voucher code: " + redemption.getVoucherCode());
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/rewards";
    }
}
