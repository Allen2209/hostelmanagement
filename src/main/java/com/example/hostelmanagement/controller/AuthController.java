package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.LoginRequest;
import com.example.hostelmanagement.dto.RegisterRequest;
import com.example.hostelmanagement.exception.StudentAlreadyExistsException;
import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Root URL redirects to login.
     */
    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("loggedInStudent") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    /**
     * Display Login Page.
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "unauthorized", required = false) String unauthorized,
            HttpSession session,
            Model model
    ) {
        // If already logged in, go straight to dashboard
        if (session.getAttribute("loggedInStudent") != null) {
            return "redirect:/dashboard";
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }
        if (unauthorized != null) {
            model.addAttribute("loginError", "Please log in to access the hostel dashboard.");
        }

        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    /**
     * Handle Login Submission.
     */
    @PostMapping("/login")
    public String processLogin(
            @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            Optional<Student> studentOpt = studentService.authenticate(
                    loginRequest.getName(),
                    loginRequest.getDateOfBirth()
            );

            if (studentOpt.isEmpty()) {
                model.addAttribute("loginError", "Invalid Name or Date of Birth. Please check your credentials or register.");
                return "login";
            }

            // Authentication successful -> store student in session
            session.setAttribute("loggedInStudent", studentOpt.get());
            return "redirect:/dashboard";

        } catch (Exception ex) {
            model.addAttribute("loginError", "Unable to log in due to a database/server issue. Please verify database connection.");
            return "login";
        }
    }

    /**
     * Display Registration Page.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    /**
     * Handle Registration Submission.
     */
    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            Student savedStudent = studentService.registerStudent(registerRequest);

            // Registration successful -> render success state with generated account number
            model.addAttribute("registrationSuccess", true);
            model.addAttribute("registeredStudent", savedStudent);
            model.addAttribute("accountNumber", savedStudent.getAccountNumber());
            return "register";

        } catch (StudentAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("existingAccount", e.getExistingAccountNumber());
            return "register";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to save registration: " + e.getMessage() + ". Please verify MySQL is running.");
            return "register";
        }
    }

    /**
     * Handle Logout.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInStudent");
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
