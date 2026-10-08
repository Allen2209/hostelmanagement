package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.RegisterRequest;
import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated()
                ? "redirect:/login" : "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "redirect:/admin/dashboard" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                @RequestParam(required = false) String denied,
                                Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/home";
        }
        if (error != null) model.addAttribute("loginError", "Incorrect student ID or password.");
        if (logout != null) model.addAttribute("logoutMessage", "You have been logged out successfully.");
        if (denied != null) model.addAttribute("loginError", "You do not have permission to access that page.");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "register";
        try {
            Student saved = studentService.registerStudent(request);
            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            redirectAttributes.addFlashAttribute("accountNumber", saved.getAccountNumber());
            redirectAttributes.addFlashAttribute("registeredName", saved.getName());
            return "redirect:/register";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}
