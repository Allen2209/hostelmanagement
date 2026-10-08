package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.service.PortalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@Controller
public class DashboardController {

    private static final Map<String, String> STUDENT_PAGES = Map.of(
            "rooms", "student/rooms",
            "room", "student/room",
            "requests", "student/requests",
            "complaints", "student/complaints",
            "profile", "student/profile"
    );

    private static final Map<String, String> ADMIN_PAGES = Map.of(
            "dashboard", "admin/dashboard",
            "hostels", "admin/hostels",
            "rooms", "admin/rooms",
            "students", "admin/students",
            "requests", "admin/requests",
            "allocations", "admin/allocations",
            "complaints", "admin/complaints",
            "profile", "admin/profile"
    );

    private final PortalService portalService;

    public DashboardController(PortalService portalService) {
        this.portalService = portalService;
    }

    /**
     * Display logged-in student's hostel dashboard.
     */
    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Student currentStudent = getCurrentStudent(userDetails);
        if (currentStudent == null) {
            return "redirect:/login?denied=true";
        }

        model.addAttribute("student", currentStudent);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pendingComplaints", portalService.countPendingComplaints(userDetails.getUsername()));
        model.addAttribute("pendingLeaves", portalService.countPendingLeaves(userDetails.getUsername()));
        model.addAttribute("pendingFees", portalService.countPendingFees(userDetails.getUsername()));
        model.addAttribute("notifications", portalService.notificationsFor(userDetails.getUsername()).stream().limit(5).toList());
        model.addAttribute("latestRoomRequest", portalService.requestsFor(userDetails.getUsername()).stream().findFirst().orElse(null));
        model.addAttribute("messPreference", portalService.preferenceFor(userDetails.getUsername()));
        return "dashboard";
    }

    @GetMapping("/student/{page}")
    public String showStudentPage(@PathVariable String page,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        Student currentStudent = getCurrentStudent(userDetails);
        if (currentStudent == null) {
            return "redirect:/login?denied=true";
        }

        String template = STUDENT_PAGES.get(page);
        if (template == null) {
            return "error";
        }

        model.addAttribute("student", currentStudent);
        model.addAttribute("activePage", page);
        return template;
    }

    @GetMapping("/admin/{page}")
    public String showAdminPage(@PathVariable String page,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        String template = ADMIN_PAGES.get(page);
        if (template == null) {
            return "error";
        }

        model.addAttribute("activePage", page);
        model.addAttribute("adminName", userDetails.getUsername());
        if ("dashboard".equals(page)) {
            model.addAttribute("totalStudents", portalService.countStudents());
            model.addAttribute("totalHostels", portalService.countHostels());
            model.addAttribute("totalRooms", portalService.countRooms());
            model.addAttribute("availableRooms", portalService.countAvailableRooms());
            model.addAttribute("fullRooms", portalService.countFullRooms());
            model.addAttribute("pendingRequests", portalService.countPendingRequests());
            model.addAttribute("pendingComplaints", portalService.countPendingComplaints());
            model.addAttribute("pendingLeaves", portalService.countPendingLeaves());
            model.addAttribute("pendingFees", portalService.countPendingFees());
            model.addAttribute("recentRequests", portalService.recentRequests());
            model.addAttribute("recentComplaints", portalService.recentComplaints());
            model.addAttribute("recentLeaves", portalService.recentLeaves());
            model.addAttribute("vegetarianCount", portalService.countPreference("VEGETARIAN"));
            model.addAttribute("nonVegetarianCount", portalService.countPreference("NON-VEGETARIAN"));
            model.addAttribute("veganCount", portalService.countPreference("VEGAN"));
        }
        return template;
    }

    private Student getCurrentStudent(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return portalService.studentForUsername(userDetails.getUsername());
    }
}
