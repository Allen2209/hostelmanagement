package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.PortalForms.*;
import com.example.hostelmanagement.dto.RegisterRequest;
import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unused")
class PortalTemplateTest {

    @Test
    void studentAndAdminPortalPagesRenderWithSharedNavigation() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        Student student = new Student(
                "HOSTEL000001",
                "Alex Rivera",
                LocalDate.of(2003, 8, 15),
                "124 Campus Avenue",
                "Computer Science"
        );
        student.setCreatedAt(LocalDate.of(2026, 10, 8).atStartOfDay());

        MockServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext springContext = new GenericWebApplicationContext();
        springContext.setServletContext(servletContext);
        springContext.refresh();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, springContext);
        JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(servletContext);
        List<String> templates = List.of(
                "dashboard",
                "student/rooms",
                "student/room",
                "student/requests",
                "student/complaints",
                "student/profile",
                "student/mess",
                "student/leave",
                "student/fees",
                "student/notifications",
                "student/change-password",
                "admin/dashboard",
                "admin/hostels",
                "admin/rooms",
                "admin/students",
                "admin/requests",
                "admin/allocations",
                "admin/complaints",
                "admin/profile",
                "admin/leave",
                "admin/mess",
                "admin/fees"
        );

        for (String template : templates) {
            MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
            MockHttpServletResponse response = new MockHttpServletResponse();
            Map<String, Object> model = new HashMap<>();
            model.put("student", student);
            model.put("complaintForm", new ComplaintForm());
            model.put("profileForm", new ProfileForm());
            model.put("messForm", new MessForm());
            model.put("leaveForm", new LeaveForm());
            model.put("passwordForm", new PasswordForm());
            model.put("requestForm", new RoomRequestForm());
            model.put("hostelForm", new HostelForm());
            model.put("roomForm", new RoomForm());
            model.put("studentForm", new RegisterRequest());
            model.put("menuForm", new MenuForm());
            model.put("feeForm", new FeeForm());
            RequestContext requestContext = new RequestContext(request, response, servletContext, model);
            WebContext context = new WebContext(
                    webApplication.buildExchange(request, response),
                    Locale.ENGLISH
            );
            if (!template.startsWith("admin/")) {
                context.setVariable("student", student);
            }
            context.setVariable("thymeleafRequestContext", new SpringWebMvcThymeleafRequestContext(requestContext, request));
            context.setVariable("complaintForm", new ComplaintForm());
            context.setVariable("profileForm", new ProfileForm());
            context.setVariable("messForm", new MessForm());
            context.setVariable("leaveForm", new LeaveForm());
            context.setVariable("passwordForm", new PasswordForm());
            context.setVariable("requestForm", new RoomRequestForm());
            context.setVariable("hostelForm", new HostelForm());
            context.setVariable("roomForm", new RoomForm());
            context.setVariable("studentForm", new RegisterRequest());
            context.setVariable("menuForm", new MenuForm());
            context.setVariable("feeForm", new FeeForm());
            context.setVariable("hostels", List.of());
            context.setVariable("rooms", List.of());
            context.setVariable("requests", List.of());
            context.setVariable("complaints", List.of());
            context.setVariable("leaves", List.of());
            context.setVariable("fees", List.of());
            context.setVariable("menus", List.of());
            context.setVariable("preferences", List.of());
            context.setVariable("students", List.of());
            context.setVariable("notifications", List.of());
            context.setVariable("roommates", List.of());
            context.setVariable("allocatedStudents", List.of());
            context.setVariable("q", "");
            context.setVariable("adminName", "Administrator");
            context.setVariable("vegetarianCount", 0);
            context.setVariable("nonVegetarianCount", 0);
            context.setVariable("veganCount", 0);
            context.setVariable("recentRequests", List.of());
            context.setVariable("recentComplaints", List.of());
            context.setVariable("recentLeaves", List.of());
            context.setVariable(
                    "activePage",
                    template.contains("/") ? template.substring(template.indexOf('/') + 1) : "dashboard"
            );

            String rendered = engine.process(template, context);

            assertTrue(rendered.contains("HostelHub"), template + " should include the shared portal brand");
            assertTrue(rendered.contains("portalSidebar"), template + " should include the shared sidebar");
            if (template.startsWith("admin/")) {
                assertTrue(rendered.contains("Administrator"), template + " should use the admin fallback identity");
            }
            springContext.close();
        }
    }
}
