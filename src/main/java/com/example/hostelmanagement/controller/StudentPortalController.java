package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.PortalForms.*;
import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.service.PortalService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentPortalController {
    private final PortalService portal;
    public StudentPortalController(PortalService portal) { this.portal = portal; }

    private String username(UserDetails user) { return user.getUsername(); }
    private void common(Model model, UserDetails user, String active) {
        model.addAttribute("student", portal.studentForUsername(username(user)));
        model.addAttribute("activePage", active);
    }
    private String back(String path, RedirectAttributes flash, RuntimeException ex) {
        flash.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + path;
    }

    @GetMapping("/rooms")
    public String rooms(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "rooms");
        model.addAttribute("rooms", portal.availableRooms());
        model.addAttribute("requestForm", new RoomRequestForm());
        return "student/rooms";
    }
    @PostMapping("/room-request")
    public String requestRoom(@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("requestForm") RoomRequestForm form,
                              BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return back("/student/rooms", flash, new IllegalArgumentException("Choose a valid room."));
        try {
            portal.requestRoom(username(user), form);
            flash.addFlashAttribute("successMessage", "Room request submitted. You can track its status here.");
            return "redirect:/student/requests";
        } catch (RuntimeException ex) { return back("/student/rooms", flash, ex); }
    }
    @GetMapping("/room")
    public String myRoom(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "room");
        model.addAttribute("roommates", portal.roommatesFor(username(user)));
        return "student/room";
    }
    @GetMapping("/requests")
    public String requests(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "requests");
        model.addAttribute("requests", portal.requestsFor(username(user)));
        return "student/requests";
    }
    @GetMapping("/mess")
    public String mess(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "mess");
        model.addAttribute("preference", portal.preferenceFor(username(user)));
        model.addAttribute("menus", portal.menus());
        model.addAttribute("messForm", new MessForm());
        return "student/mess";
    }
    @PostMapping("/mess")
    public String saveMess(@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("messForm") MessForm form,
                           BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return back("/student/mess", flash, new IllegalArgumentException("Select your food preference."));
        try {
            portal.savePreference(username(user), form);
            flash.addFlashAttribute("successMessage", "Mess preference saved.");
            return "redirect:/student/mess";
        } catch (RuntimeException ex) { return back("/student/mess", flash, ex); }
    }
    @GetMapping("/complaints")
    public String complaints(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "complaints");
        model.addAttribute("complaints", portal.complaintsFor(username(user)));
        model.addAttribute("complaintForm", new ComplaintForm());
        return "student/complaints";
    }
    @PostMapping("/complaints")
    public String submitComplaint(@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("complaintForm") ComplaintForm form,
                                  BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return back("/student/complaints", flash, new IllegalArgumentException("Complete all complaint fields."));
        try {
            portal.submitComplaint(username(user), form);
            flash.addFlashAttribute("successMessage", "Your complaint has been submitted.");
            return "redirect:/student/complaints";
        } catch (RuntimeException ex) { return back("/student/complaints", flash, ex); }
    }
    @GetMapping("/leave")
    public String leave(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "leave");
        model.addAttribute("leaves", portal.leavesFor(username(user)));
        model.addAttribute("leaveForm", new LeaveForm());
        return "student/leave";
    }
    @PostMapping("/leave")
    public String applyLeave(@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("leaveForm") LeaveForm form,
                             BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return back("/student/leave", flash, new IllegalArgumentException("Complete the leave form with valid dates and contact details."));
        try {
            portal.applyLeave(username(user), form);
            flash.addFlashAttribute("successMessage", "Your leave request has been submitted.");
            return "redirect:/student/leave";
        } catch (RuntimeException ex) { return back("/student/leave", flash, ex); }
    }
    @GetMapping("/fees")
    public String fees(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "fees");
        model.addAttribute("fees", portal.feesFor(username(user)));
        return "student/fees";
    }
    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "notifications");
        model.addAttribute("notifications", portal.notificationsFor(username(user)));
        return "student/notifications";
    }
    @PostMapping("/notifications/{id}/read")
    public String markRead(@AuthenticationPrincipal UserDetails user, @PathVariable Long id, RedirectAttributes flash) {
        try { portal.markNotificationRead(username(user), id); }
        catch (RuntimeException ex) { flash.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/student/notifications";
    }
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "profile");
        Student student = portal.studentForUsername(username(user));
        ProfileForm form = new ProfileForm();
        form.setName(student.getName());
        form.setEmail(student.getEmail());
        form.setPhone(student.getPhone());
        form.setDepartmentName(student.getDepartmentName());
        form.setAcademicYear(student.getAcademicYear());
        form.setGender(student.getGender());
        form.setHomeAddress(student.getHomeAddress());
        form.setParentName(student.getParentName());
        form.setParentPhone(student.getParentPhone());
        model.addAttribute("profileForm", form);
        return "student/profile";
    }
    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return back("/student/profile", flash, new IllegalArgumentException("Check the profile fields and try again."));
        try {
            portal.updateProfile(username(user), form);
            flash.addFlashAttribute("successMessage", "Profile updated.");
            return "redirect:/student/profile";
        } catch (RuntimeException ex) { return back("/student/profile", flash, ex); }
    }
    @GetMapping("/change-password")
    public String password(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "password");
        model.addAttribute("passwordForm", new PasswordForm());
        return "student/change-password";
    }
    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("passwordForm") PasswordForm form,
                                 BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return back("/student/change-password", flash, new IllegalArgumentException("Password must be at least 8 characters."));
        try {
            portal.changePassword(username(user), form);
            flash.addFlashAttribute("successMessage", "Password changed successfully.");
            return "redirect:/student/change-password";
        } catch (RuntimeException ex) { return back("/student/change-password", flash, ex); }
    }
}
