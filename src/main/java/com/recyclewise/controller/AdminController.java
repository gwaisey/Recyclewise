package com.recyclewise.controller;

import com.recyclewise.model.TrashSubmission;
import com.recyclewise.repository.TrashSubmissionRepository;
import com.recyclewise.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String ADMIN_PIN = "recyclewise2026";
    private final TrashSubmissionRepository submissionRepository;
    private final UserService userService;

    @GetMapping("/login")
    public String adminLoginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) return "redirect:/admin/submissions";
        return "pages/admin-login";
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String pin, HttpSession session, RedirectAttributes ra) {
        if (ADMIN_PIN.equals(pin)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/admin/submissions";
        }
        ra.addFlashAttribute("error", "Invalid PIN. Access denied.");
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("isAdmin");
        return "redirect:/admin/login";
    }

    @GetMapping("/submissions")
    public String submissions(HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) return "redirect:/admin/login";
        model.addAttribute("pending", submissionRepository.findByStatusOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.PENDING));
        model.addAttribute("resolved", submissionRepository.findByStatusNotOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.PENDING));
        return "pages/admin-submissions";
    }

    @PostMapping("/submissions/{id}/confirm")
    public String confirm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) return "redirect:/admin/login";
        TrashSubmission sub = submissionRepository.findById(id).orElseThrow();
        sub.setStatus(TrashSubmission.SubmissionStatus.CONFIRMED);
        submissionRepository.save(sub);
        ra.addFlashAttribute("success", "Confirmed! Points awarded to " + sub.getUser().getUsername());
        return "redirect:/admin/submissions";
    }

    @PostMapping("/submissions/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) return "redirect:/admin/login";
        TrashSubmission sub = submissionRepository.findById(id).orElseThrow();
        if (sub.getStatus() == TrashSubmission.SubmissionStatus.PENDING) {
            userService.deductPoints(sub.getUser(), sub.getPointsEarned());
            sub.setStatus(TrashSubmission.SubmissionStatus.REJECTED);
            submissionRepository.save(sub);
            ra.addFlashAttribute("error", "Rejected. Points deducted from " + sub.getUser().getUsername());
        }
        return "redirect:/admin/submissions";
    }
}
