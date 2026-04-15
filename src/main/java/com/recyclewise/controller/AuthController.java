package com.recyclewise.controller;

import com.recyclewise.model.User;
import com.recyclewise.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(HttpSession session, org.springframework.ui.Model model) {
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        model.addAttribute("activePage", "register");
        return "pages/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam @NotBlank(message = "Username is required") @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores") String username,
                           @RequestParam @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
                           @RequestParam @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password,
                           @RequestParam @NotBlank(message = "Full name is required") @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters") String fullName,
                           RedirectAttributes ra) {
        try {
            userService.register(username, email, password, fullName);
            ra.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "An unexpected error occurred. Please check your details and try again.");
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, org.springframework.ui.Model model) {
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        model.addAttribute("activePage", "login");
        return "pages/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam @NotBlank(message = "Username is required") @Size(min = 3, max = 30) String username,
                        @RequestParam @NotBlank(message = "Password is required") @Size(min = 1) String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
