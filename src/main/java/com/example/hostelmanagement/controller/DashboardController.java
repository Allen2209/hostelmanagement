package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {

    private final StudentService studentService;

    public DashboardController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Display logged-in student's hostel dashboard.
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Student sessionStudent = (Student) session.getAttribute("loggedInStudent");

        if (sessionStudent == null) {
            return "redirect:/login?unauthorized=true";
        }

        // Fetch latest details from database if available, otherwise use session student
        Optional<Student> freshStudent = studentService.findByAccountNumber(sessionStudent.getAccountNumber());
        Student currentStudent = freshStudent.orElse(sessionStudent);

        model.addAttribute("student", currentStudent);
        return "dashboard";
    }
}
