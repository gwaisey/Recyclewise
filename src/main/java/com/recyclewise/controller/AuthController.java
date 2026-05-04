package com.recyclewise.controller;

import com.recyclewise.model.User;
import com.recyclewise.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    public String register(@Valid @ModelAttribute com.recyclewise.dto.RegisterRequest request,
                           BindingResult result,
                           RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/register";
        }

        try {
            userService.register(request.getUsername(), request.getEmail(), request.getPassword(), request.getFullName());
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
    public String login(@RequestParam String username,
                        @RequestParam String password,
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
