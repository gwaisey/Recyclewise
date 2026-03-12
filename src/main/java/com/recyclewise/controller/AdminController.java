package com.recyclewise.controller;

import com.recyclewise.model.AdminUser;
import com.recyclewise.model.TrashSubmission;
import com.recyclewise.repository.AdminUserRepository;
import com.recyclewise.repository.TrashSubmissionRepository;
import com.recyclewise.repository.TrashStationRepository;
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

    private final AdminUserRepository adminUserRepository;
    private final TrashSubmissionRepository submissionRepository;
    private final TrashStationRepository stationRepository;
    private final UserService userService;

    // ── Auth ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("adminId") != null) return "redirect:/admin/submissions";
        return "pages/admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        AdminUser admin = adminUserRepository.findByEmailAndActiveTrue(email).orElse(null);
        if (admin == null || !admin.getPassword().equals(password)) {
            ra.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/admin/login";
        }
        session.setAttribute("adminId", admin.getId());
        session.setAttribute("adminName", admin.getFullName());
        session.setAttribute("adminRole", admin.getRole().name());
        if (admin.getRole() == AdminUser.AdminRole.SUPER_ADMIN) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/submissions";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminId");
        session.removeAttribute("adminName");
        session.removeAttribute("adminRole");
        return "redirect:/admin/login";
    }

    // ── Super Admin Dashboard ───────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String superDashboard(HttpSession session, Model model) {
        if (!isSuperAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("totalPending", submissionRepository
            .findByStatusOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.PENDING).size());
        model.addAttribute("totalConfirmed", submissionRepository
            .findByStatusOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.CONFIRMED).size());
        model.addAttribute("totalRejected", submissionRepository
            .findByStatusOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.REJECTED).size());
        model.addAttribute("staffList", adminUserRepository
            .findByRoleOrderByFullNameAsc(AdminUser.AdminRole.STATION_STAFF));
        model.addAttribute("stations", stationRepository.findAll());
        return "pages/admin-dashboard";
    }

    // ── Staff Management (Super Admin only) ─────────────────────────────────

    @PostMapping("/staff/create")
    public String createStaff(@RequestParam String fullName,
                               @RequestParam String email,
                               @RequestParam Long stationId,
                               HttpSession session,
                               RedirectAttributes ra) {
        if (!isSuperAdmin(session)) return "redirect:/admin/login";
        if (adminUserRepository.findByEmailAndActiveTrue(email).isPresent()) {
            ra.addFlashAttribute("error", "Email already in use.");
            return "redirect:/admin/dashboard";
        }
        var station = stationRepository.findById(stationId).orElseThrow();
        adminUserRepository.save(AdminUser.builder()
            .fullName(fullName)
            .email(email)
            .password("Staff@2026")
            .role(AdminUser.AdminRole.STATION_STAFF)
            .assignedStation(station)
            .active(true)
            .build());
        ra.addFlashAttribute("success", fullName + " added as staff for " + station.getName() + ". Default password: Staff@2026");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/staff/{id}/deactivate")
    public String deactivateStaff(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isSuperAdmin(session)) return "redirect:/admin/login";
        AdminUser staff = adminUserRepository.findById(id).orElseThrow();
        staff.setActive(false);
        adminUserRepository.save(staff);
        ra.addFlashAttribute("success", staff.getFullName() + " has been deactivated.");
        return "redirect:/admin/dashboard";
    }

    // ── Submissions ─────────────────────────────────────────────────────────

    @GetMapping("/submissions")
    public String submissions(HttpSession session, Model model) {
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) return "redirect:/admin/login";

        AdminUser admin = adminUserRepository.findById(adminId).orElse(null);
        if (admin == null) return "redirect:/admin/login";

        List<TrashSubmission> pending;
        List<TrashSubmission> resolved;

        if (admin.getRole() == AdminUser.AdminRole.SUPER_ADMIN) {
            // Super admin sees ALL submissions
            pending = submissionRepository.findByStatusOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.PENDING);
            resolved = submissionRepository.findByStatusNotOrderBySubmittedAtDesc(TrashSubmission.SubmissionStatus.PENDING);
        } else {
            // Station staff sees ONLY their station's submissions
            Long stationId = admin.getAssignedStation().getId();
            pending = submissionRepository.findByStationIdAndStatusOrderBySubmittedAtDesc(stationId, TrashSubmission.SubmissionStatus.PENDING);
            resolved = submissionRepository.findByStationIdAndStatusNotOrderBySubmittedAtDesc(stationId, TrashSubmission.SubmissionStatus.PENDING);
        }

        model.addAttribute("pending", pending);
        model.addAttribute("resolved", resolved);
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminRole", session.getAttribute("adminRole"));
        model.addAttribute("stationName", admin.getRole() == AdminUser.AdminRole.STATION_STAFF
            ? admin.getAssignedStation().getName() : "All Stations");
        return "pages/admin-submissions";
    }

    @PostMapping("/submissions/{id}/confirm")
    public String confirm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("adminId") == null) return "redirect:/admin/login";
        TrashSubmission sub = submissionRepository.findById(id).orElseThrow();
        sub.setStatus(TrashSubmission.SubmissionStatus.CONFIRMED);
        submissionRepository.save(sub);
        userService.addPoints(sub.getUser(), sub.getPointsEarned());
        ra.addFlashAttribute("success", "Confirmed! +" + sub.getPointsEarned() + " pts awarded to " + sub.getUser().getUsername());
        return "redirect:/admin/submissions";
    }

    @PostMapping("/submissions/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("adminId") == null) return "redirect:/admin/login";
        TrashSubmission sub = submissionRepository.findById(id).orElseThrow();
        if (sub.getStatus() == TrashSubmission.SubmissionStatus.PENDING) {
            sub.setStatus(TrashSubmission.SubmissionStatus.REJECTED);
            submissionRepository.save(sub);
            ra.addFlashAttribute("error", "Submission rejected for " + sub.getUser().getUsername());
        }
        return "redirect:/admin/submissions";
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private boolean isSuperAdmin(HttpSession session) {
        return "SUPER_ADMIN".equals(session.getAttribute("adminRole")) && session.getAttribute("adminId") != null;
    }
}
