package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.PortalForms.*;
import com.example.hostelmanagement.dto.RegisterRequest;
import com.example.hostelmanagement.service.PortalService;
import com.example.hostelmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminPortalController {
    private final PortalService portal;
    private final StudentService studentService;
    public AdminPortalController(PortalService portal, StudentService studentService) {
        this.portal = portal;
        this.studentService = studentService;
    }
    private void common(Model model, UserDetails user, String active) {
        model.addAttribute("activePage", active);
        model.addAttribute("adminName", user.getUsername());
    }
    private String fail(String path, RedirectAttributes flash, RuntimeException ex) {
        flash.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + path;
    }

    @GetMapping("/hostels")
    public String hostels(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "hostels");
        model.addAttribute("hostels", portal.hostels());
        model.addAttribute("hostelForm", new HostelForm());
        return "admin/hostels";
    }
    @PostMapping("/hostels/save")
    public String saveHostel(@AuthenticationPrincipal UserDetails user, @RequestParam(required = false) Long id,
                             @Valid @ModelAttribute("hostelForm") HostelForm form, BindingResult errors,
                             RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/hostels", flash, new IllegalArgumentException("Enter a valid hostel name."));
        try { portal.saveHostel(id, form); flash.addFlashAttribute("successMessage", "Hostel saved."); return "redirect:/admin/hostels"; }
        catch (RuntimeException ex) { return fail("/admin/hostels", flash, ex); }
    }
    @PostMapping("/hostels/{id}/deactivate")
    public String deactivateHostel(@PathVariable Long id, RedirectAttributes flash) {
        try { portal.deactivateHostel(id); flash.addFlashAttribute("successMessage", "Hostel deactivated."); return "redirect:/admin/hostels"; }
        catch (RuntimeException ex) { return fail("/admin/hostels", flash, ex); }
    }

    @GetMapping("/rooms")
    public String rooms(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "rooms");
        model.addAttribute("rooms", portal.rooms());
        model.addAttribute("hostels", portal.hostels());
        model.addAttribute("roomForm", new RoomForm());
        return "admin/rooms";
    }
    @PostMapping("/rooms/save")
    public String saveRoom(@RequestParam(required = false) Long id, @Valid @ModelAttribute("roomForm") RoomForm form,
                           BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/rooms", flash, new IllegalArgumentException("Enter a valid room and capacity."));
        try { portal.saveRoom(id, form); flash.addFlashAttribute("successMessage", "Room saved."); return "redirect:/admin/rooms"; }
        catch (RuntimeException ex) { return fail("/admin/rooms", flash, ex); }
    }
    @PostMapping("/rooms/{id}/deactivate")
    public String deactivateRoom(@PathVariable Long id, RedirectAttributes flash) {
        try { portal.deactivateRoom(id); flash.addFlashAttribute("successMessage", "Room moved to maintenance."); return "redirect:/admin/rooms"; }
        catch (RuntimeException ex) { return fail("/admin/rooms", flash, ex); }
    }

    @GetMapping("/students")
    public String students(@AuthenticationPrincipal UserDetails user, @RequestParam(required = false) String q, Model model) {
        common(model, user, "students");
        model.addAttribute("students", portal.studentList(q));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("studentForm", new RegisterRequest());
        return "admin/students";
    }
    @PostMapping("/students/save")
    public String addStudent(@Valid @ModelAttribute("studentForm") RegisterRequest form, BindingResult errors,
                             RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/students", flash, new IllegalArgumentException("Complete all required student fields."));
        try {
            studentService.registerStudent(form);
            flash.addFlashAttribute("successMessage", "Student account created.");
            return "redirect:/admin/students";
        } catch (RuntimeException ex) { return fail("/admin/students", flash, ex); }
    }
    @PostMapping("/students/{id}/deactivate")
    public String deactivateStudent(@PathVariable Long id, RedirectAttributes flash) {
        try { portal.deactivateStudent(id); flash.addFlashAttribute("successMessage", "Student account deactivated."); return "redirect:/admin/students"; }
        catch (RuntimeException ex) { return fail("/admin/students", flash, ex); }
    }

    @PostMapping("/students/{id}/edit")
    public String editStudent(@PathVariable Long id, @Valid @ModelAttribute("profileForm") ProfileForm form,
                              BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/students", flash, new IllegalArgumentException("Complete the student profile fields."));
        try {
            portal.updateStudentByAdmin(id, form);
            flash.addFlashAttribute("successMessage", "Student profile updated.");
            return "redirect:/admin/students";
        } catch (RuntimeException ex) { return fail("/admin/students", flash, ex); }
    }

    @GetMapping({"/room-requests", "/requests"})
    public String requests(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "requests");
        model.addAttribute("requests", portal.requests());
        model.addAttribute("reviewForm", new ReviewForm());
        return "admin/requests";
    }
    @PostMapping("/room-requests/{id}/review")
    public String reviewRequest(@PathVariable Long id, @Valid @ModelAttribute("reviewForm") ReviewForm form,
                                BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/room-requests", flash, new IllegalArgumentException("Choose approve or reject."));
        try { portal.reviewRoomRequest(id, form); flash.addFlashAttribute("successMessage", "Room request updated."); return "redirect:/admin/room-requests"; }
        catch (RuntimeException ex) { return fail("/admin/room-requests", flash, ex); }
    }

    @GetMapping("/complaints")
    public String complaints(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "complaints");
        model.addAttribute("complaints", portal.complaints());
        return "admin/complaints";
    }
    @PostMapping("/complaints/{id}/review")
    public String reviewComplaint(@PathVariable Long id, @RequestParam String status,
                                  @RequestParam(required = false) String remarks, RedirectAttributes flash) {
        ReviewForm form = new ReviewForm(); form.setStatus(status); form.setRemarks(remarks);
        try { portal.updateComplaint(id, form); flash.addFlashAttribute("successMessage", "Complaint status updated."); return "redirect:/admin/complaints"; }
        catch (RuntimeException ex) { return fail("/admin/complaints", flash, ex); }
    }
    @GetMapping("/leave")
    public String leaves(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "leave");
        model.addAttribute("leaves", portal.leaves());
        return "admin/leave";
    }

    @GetMapping("/allocations")
    public String allocations(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "allocations");
        model.addAttribute("allocatedStudents", portal.allocatedStudents());
        return "admin/allocations";
    }
    @PostMapping("/leave/{id}/review")
    public String reviewLeave(@PathVariable Long id, @RequestParam String status,
                              @RequestParam(required = false) String remarks, RedirectAttributes flash) {
        ReviewForm form = new ReviewForm(); form.setStatus(status); form.setRemarks(remarks);
        try { portal.reviewLeave(id, form); flash.addFlashAttribute("successMessage", "Leave request updated."); return "redirect:/admin/leave"; }
        catch (RuntimeException ex) { return fail("/admin/leave", flash, ex); }
    }

    @GetMapping("/mess")
    public String mess(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "mess");
        model.addAttribute("menus", portal.menus());
        model.addAttribute("preferences", portal.preferences());
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("vegetarianCount", portal.countPreference("VEGETARIAN"));
        model.addAttribute("nonVegetarianCount", portal.countPreference("NON-VEGETARIAN"));
        model.addAttribute("veganCount", portal.countPreference("VEGAN"));
        return "admin/mess";
    }
    @PostMapping("/mess/save")
    public String saveMenu(@RequestParam(required = false) Long id, @Valid @ModelAttribute("menuForm") MenuForm form,
                           BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/mess", flash, new IllegalArgumentException("Complete the menu fields."));
        try { portal.saveMenu(id, form); flash.addFlashAttribute("successMessage", "Mess menu saved."); return "redirect:/admin/mess"; }
        catch (RuntimeException ex) { return fail("/admin/mess", flash, ex); }
    }
    @PostMapping("/mess/{id}/delete")
    public String deleteMenu(@PathVariable Long id, RedirectAttributes flash) {
        try { portal.deleteMenu(id); flash.addFlashAttribute("successMessage", "Menu deleted."); return "redirect:/admin/mess"; }
        catch (RuntimeException ex) { return fail("/admin/mess", flash, ex); }
    }

    @GetMapping("/fees")
    public String fees(@AuthenticationPrincipal UserDetails user, Model model) {
        common(model, user, "fees");
        model.addAttribute("fees", portal.fees());
        model.addAttribute("students", portal.studentList(null));
        model.addAttribute("feeForm", new FeeForm());
        return "admin/fees";
    }
    @PostMapping("/fees/save")
    public String addFee(@Valid @ModelAttribute("feeForm") FeeForm form, BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return fail("/admin/fees", flash, new IllegalArgumentException("Enter a valid student, amount, and due date."));
        try { portal.addFee(form); flash.addFlashAttribute("successMessage", "Fee added and student notified."); return "redirect:/admin/fees"; }
        catch (RuntimeException ex) { return fail("/admin/fees", flash, ex); }
    }
    @PostMapping("/fees/{id}/payment")
    public String updatePayment(@PathVariable Long id, @RequestParam BigDecimal paidAmount, RedirectAttributes flash) {
        try { portal.updateFeePayment(id, paidAmount); flash.addFlashAttribute("successMessage", "Fee payment record updated."); return "redirect:/admin/fees"; }
        catch (RuntimeException ex) { return fail("/admin/fees", flash, ex); }
    }
    @PostMapping("/notifications/broadcast")
    public String broadcast(@RequestParam String title, @RequestParam String message, RedirectAttributes flash) {
        try { portal.broadcast(title, message); flash.addFlashAttribute("successMessage", "Announcement sent to students."); return "redirect:/admin/dashboard"; }
        catch (RuntimeException ex) { return fail("/admin/dashboard", flash, ex); }
    }
}
