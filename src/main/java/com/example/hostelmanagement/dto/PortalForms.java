package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class PortalForms {
    private PortalForms() { }

    @Getter @Setter @NoArgsConstructor
    public static class RoomRequestForm {
        @NotNull private Long roomId;
        @Size(max = 500) private String reason;
    }

    @Getter @Setter @NoArgsConstructor
    public static class ComplaintForm {
        @NotBlank private String category;
        @NotBlank @Size(max = 150) private String subject;
        @NotBlank @Size(max = 2000) private String description;
    }

    @Getter @Setter @NoArgsConstructor
    public static class LeaveForm {
        @NotNull private LocalDate fromDate;
        @NotNull private LocalDate toDate;
        @NotBlank @Size(max = 1000) private String reason;
        @NotBlank @Size(max = 100) private String parentName;
        @NotBlank @Pattern(regexp = "^[0-9+() .-]{7,20}$") private String parentPhone;
    }

    @Getter @Setter @NoArgsConstructor
    public static class MessForm {
        @NotBlank private String foodPreference;
        private boolean breakfast;
        private boolean lunch;
        private boolean snacks;
        private boolean dinner;
    }

    @Getter @Setter @NoArgsConstructor
    public static class MenuForm {
        @NotNull private LocalDate menuDate;
        @NotBlank @Size(max = 1000) private String breakfast;
        @NotBlank @Size(max = 1000) private String lunch;
        @NotBlank @Size(max = 1000) private String snacks;
        @NotBlank @Size(max = 1000) private String dinner;
    }

    @Getter @Setter @NoArgsConstructor
    public static class FeeForm {
        @NotNull private Long studentId;
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
        @NotNull @DecimalMin("0.00") private BigDecimal paidAmount = BigDecimal.ZERO;
        @NotNull private LocalDate dueDate;
    }

    @Getter @Setter @NoArgsConstructor
    public static class ReviewForm {
        @NotBlank private String status;
        @Size(max = 500) private String remarks;
    }

    @Getter @Setter @NoArgsConstructor
    public static class HostelForm {
        @NotBlank @Size(max = 100) private String name;
        @Size(max = 200) private String location;
        @Size(max = 1000) private String description;
        @NotBlank private String status = "ACTIVE";
    }

    @Getter @Setter @NoArgsConstructor
    public static class RoomForm {
        @NotNull private Long hostelId;
        @NotBlank @Size(max = 30) private String roomNumber;
        @Min(1) private int capacity = 1;
        @NotBlank private String roomType;
        @NotBlank private String status = "AVAILABLE";
    }

    @Getter @Setter @NoArgsConstructor
    public static class ProfileForm {
        @NotBlank @Size(max = 100) private String name;
        @NotBlank @Email private String email;
        @NotBlank @Pattern(regexp = "^[0-9+() .-]{7,20}$") private String phone;
        @NotBlank private String departmentName;
        @NotBlank private String academicYear;
        @NotBlank private String gender;
        @NotBlank @Size(max = 500) private String homeAddress;
        @NotBlank private String parentName;
        @NotBlank @Pattern(regexp = "^[0-9+() .-]{7,20}$") private String parentPhone;
    }

    @Getter @Setter @NoArgsConstructor
    public static class PasswordForm {
        @NotBlank private String currentPassword;
        @NotBlank @Size(min = 8, max = 72) private String newPassword;
        @NotBlank private String confirmPassword;
    }
}
