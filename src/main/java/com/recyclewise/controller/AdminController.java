package com.recyclewise.controller;

import com.recyclewise.model.AdminUser;
import com.recyclewise.model.TrashSubmission;
import com.recyclewise.repository.AdminUserRepository;
import com.recyclewise.repository.TrashSubmissionRepository;
import com.recyclewise.repository.TrashStationRepository;
import com.recyclewise.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserRepository adminUserRepository;
    private final TrashSubmissionRepository submissionRepository;
    private final TrashStationRepository stationRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // ── Auth ────────────────────────────────────────────────────────────────

    @GetMapping("")
    public String adminRoot(HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        String role = (String) session.getAttribute("adminRole");
        if ("SUPER_ADMIN".equals(role)) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/submissions";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("adminId") != null) {
            String role = (String) session.getAttribute("adminRole");
            if ("SUPER_ADMIN".equals(role)) return "redirect:/admin/dashboard";
            return "redirect:/admin/submissions";
        }
        return "pages/admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam @NotBlank(message = "Email is required") @Email(message = "Valid email required") String email,
                        @RequestParam @NotBlank(message = "Password is required") String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        AdminUser admin = adminUserRepository.findByEmailAndActiveTrue(email).orElse(null);
        if (admin == null) {
            ra.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/admin/login";
        }
        
        boolean passwordMatches = passwordEncoder.matches(password, admin.getPassword());
        if (!passwordMatches) {
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
            .findByStatusOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus.PENDING).size());
        model.addAttribute("totalConfirmed", submissionRepository
            .findByStatusOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus.CONFIRMED).size());
        model.addAttribute("totalRejected", submissionRepository
            .findByStatusOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus.REJECTED).size());
        model.addAttribute("staffList", adminUserRepository
            .findByRoleOrderByFullNameAsc(AdminUser.AdminRole.STATION_STAFF));
        model.addAttribute("stations", stationRepository.findAll());
        return "pages/admin-dashboard";
    }

    // ── Staff Management (Super Admin only) ─────────────────────────────────

    @PostMapping("/staff/create")
    public String createStaff(@RequestParam @NotBlank(message = "Full name is required") @Size(min = 2, max = 100) String fullName,
                               @RequestParam @NotBlank(message = "Email is required") @Email(message = "Valid email required") String email,
                               @RequestParam @NotNull(message = "Station is required") Long stationId,
                               HttpSession session,
                               RedirectAttributes ra) {
        if (!isSuperAdmin(session)) return "redirect:/admin/login";
        if (adminUserRepository.findByEmailAndActiveTrue(email).isPresent()) {
            ra.addFlashAttribute("error", "Email already in use.");
            return "redirect:/admin/dashboard";
        }
        var station = stationRepository.findById(Objects.requireNonNull(stationId)).orElseThrow();
        String tempPassword = System.getenv("STAFF_INITIAL_PASSWORD");
        if (tempPassword == null || tempPassword.isBlank()) {
            tempPassword = generateSecureTempPassword();
        }
        AdminUser newStaff = AdminUser.builder()
            .fullName(fullName)
            .email(email)
            .password(passwordEncoder.encode(tempPassword))
            .role(AdminUser.AdminRole.STATION_STAFF)
            .assignedStation(station)
            .active(true)
            .build();
        session.setAttribute("tempStaffPassword", tempPassword);
        Objects.requireNonNull(newStaff, "AdminUser creation failed");
        adminUserRepository.save(newStaff);
        ra.addFlashAttribute("success", fullName + " added as staff for " + station.getName() + ". Temp password: " + tempPassword);
        session.removeAttribute("tempStaffPassword");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/staff/{id}/deactivate")
    public String deactivateStaff(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isSuperAdmin(session)) return "redirect:/admin/login";
        Objects.requireNonNull(id, "Staff ID must not be null");
        AdminUser staff = adminUserRepository.findById(id).orElseThrow();
        staff.setActive(false);
        adminUserRepository.save(staff);
        ra.addFlashAttribute("success", staff.getFullName() + " has been deactivated.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/staff/{id}/reactivate")
    public String reactivateStaff(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isSuperAdmin(session)) return "redirect:/admin/login";
        Objects.requireNonNull(id, "Staff ID must not be null");
        AdminUser staff = adminUserRepository.findById(id).orElseThrow();
        staff.setActive(true);
        adminUserRepository.save(staff);
        ra.addFlashAttribute("success", staff.getFullName() + " has been reactivated.");
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
            pending = submissionRepository.findByStatusOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus.PENDING);
            resolved = submissionRepository.findByStatusNotOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus.PENDING);
        } else {
            // Station staff sees ONLY their station's submissions
            if (admin.getAssignedStation() == null) {
                pending = List.of();
                resolved = List.of();
            } else {
                Long stationId = admin.getAssignedStation().getId();
                pending = submissionRepository.findByStationIdAndStatusOrderByCreatedAtDesc(stationId, TrashSubmission.SubmissionStatus.PENDING);
                resolved = submissionRepository.findByStationIdAndStatusNotOrderByCreatedAtDesc(stationId, TrashSubmission.SubmissionStatus.PENDING);
            }
        }

        model.addAttribute("pending", pending);
        model.addAttribute("resolved", resolved);
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminRole", session.getAttribute("adminRole"));
        
        String stationNameDisplay = "All Stations";
        if (admin.getRole() == AdminUser.AdminRole.STATION_STAFF && admin.getAssignedStation() != null) {
            stationNameDisplay = admin.getAssignedStation().getName();
        }
        model.addAttribute("stationName", stationNameDisplay);
        return "pages/admin-submissions";
    }

    @PostMapping("/submissions/{id}/confirm")
    public String confirm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("adminId") == null) return "redirect:/admin/login";
        Objects.requireNonNull(id, "Submission ID must not be null");
        TrashSubmission sub = submissionRepository.findById(id).orElseThrow();
        if (sub.getUser() == null) {
            ra.addFlashAttribute("error", "Error: Submission has no associated user.");
            return "redirect:/admin/submissions";
        }
        sub.setStatus(TrashSubmission.SubmissionStatus.CONFIRMED);
        submissionRepository.save(sub);
        userService.addPoints(sub.getUser(), sub.getPointsEarned());
        ra.addFlashAttribute("success", "Confirmed! +" + sub.getPointsEarned() + " pts awarded to " + sub.getUser().getUsername());
        return "redirect:/admin/submissions";
    }

    @PostMapping("/submissions/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("adminId") == null) return "redirect:/admin/login";
        Objects.requireNonNull(id, "Submission ID must not be null");
        TrashSubmission sub = submissionRepository.findById(id).orElseThrow();
        if (sub.getStatus() != null && sub.getStatus() == TrashSubmission.SubmissionStatus.PENDING) {
            sub.setStatus(TrashSubmission.SubmissionStatus.REJECTED);
            submissionRepository.save(sub);
            String username = (sub.getUser() != null) ? sub.getUser().getUsername() : "Unknown User";
            ra.addFlashAttribute("error", "Submission rejected for " + username);
        }
        return "redirect:/admin/submissions";
    }

    // ── Change Password ─────────────────────────────────────────────────────

    @PostMapping("/change-password")
    public String changePassword(@RequestParam @NotBlank(message = "Current password is required") String currentPassword,
                                 @RequestParam @NotBlank(message = "New password is required") @Size(min = 8, max = 100, message = "Password must be 8-100 characters") String newPassword,
                                 @RequestParam @NotBlank(message = "Please confirm your password") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) return "redirect:/admin/login";
        
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("passwordError", "New passwords do not match.");
            return getRedirect(session);
        }
        
        AdminUser admin = adminUserRepository.findById(adminId).orElse(null);
        if (admin == null) return "redirect:/admin/login";
        
        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            ra.addFlashAttribute("passwordError", "Current password is incorrect.");
            return getRedirect(session);
        }
        
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminUserRepository.save(admin);
        
        ra.addFlashAttribute("passwordSuccess", "Password changed successfully!");
        return getRedirect(session);
    }

    private String getRedirect(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        if ("SUPER_ADMIN".equals(role)) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/submissions";
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private boolean isSuperAdmin(HttpSession session) {
        return "SUPER_ADMIN".equals(session.getAttribute("adminRole")) && session.getAttribute("adminId") != null;
    }

    private String generateSecureTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#$%";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
