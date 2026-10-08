package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.PortalForms.*;
import com.example.hostelmanagement.model.*;
import com.example.hostelmanagement.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PortalService {
    private final StudentRepository students;
    private final UserAccountRepository users;
    private final HostelRepository hostels;
    private final RoomRepository rooms;
    private final RoomRequestRepository requests;
    private final ComplaintRepository complaints;
    private final LeaveRequestRepository leaves;
    private final MessPreferenceRepository preferences;
    private final MessMenuRepository menus;
    private final FeeRepository fees;
    private final NotificationRepository notifications;
    private final PasswordEncoder passwordEncoder;

    public PortalService(StudentRepository students, UserAccountRepository users, HostelRepository hostels,
                         RoomRepository rooms, RoomRequestRepository requests, ComplaintRepository complaints,
                         LeaveRequestRepository leaves, MessPreferenceRepository preferences,
                         MessMenuRepository menus, FeeRepository fees, NotificationRepository notifications,
                         PasswordEncoder passwordEncoder) {
        this.students = students;
        this.users = users;
        this.hostels = hostels;
        this.rooms = rooms;
        this.requests = requests;
        this.complaints = complaints;
        this.leaves = leaves;
        this.preferences = preferences;
        this.menus = menus;
        this.fees = fees;
        this.notifications = notifications;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Student studentForUsername(String username) {
        return students.findByStudentIdIgnoreCase(username).or(() -> students.findByUserAccount_UsernameIgnoreCase(username))
                .orElseThrow(() -> new IllegalArgumentException("Student account was not found."));
    }

    @Transactional(readOnly = true)
    public List<Student> studentList(String search) {
        return search == null || search.isBlank() ? students.findAllByOrderByNameAsc() : students.search(search.trim());
    }
    @Transactional(readOnly = true) public List<Hostel> hostels() { return hostels.findAll(); }
    @Transactional(readOnly = true) public List<Room> rooms() { return rooms.findAllByOrderByHostel_NameAscRoomNumberAsc(); }
    @Transactional(readOnly = true) public List<Room> availableRooms() { return rooms.findByStatusAndHostel_Status("AVAILABLE", "ACTIVE").stream().filter(r -> r.getAvailableBeds() > 0).toList(); }
    @Transactional(readOnly = true) public List<RoomRequest> requests() { return requests.findAllByOrderByRequestDateDesc(); }
    @Transactional(readOnly = true) public List<RoomRequest> requestsFor(String username) { return requests.findByStudent_IdOrderByRequestDateDesc(studentForUsername(username).getId()); }
    @Transactional(readOnly = true) public List<Complaint> complaints() { return complaints.findAllByOrderByCreatedAtDesc(); }
    @Transactional(readOnly = true) public List<Complaint> complaintsFor(String username) { return complaints.findByStudent_IdOrderByCreatedAtDesc(studentForUsername(username).getId()); }
    @Transactional(readOnly = true) public List<LeaveRequest> leaves() { return leaves.findAllByOrderByCreatedAtDesc(); }
    @Transactional(readOnly = true) public List<LeaveRequest> leavesFor(String username) { return leaves.findByStudent_IdOrderByCreatedAtDesc(studentForUsername(username).getId()); }
    @Transactional(readOnly = true) public List<MessMenu> menus() { return menus.findAllByOrderByMenuDateAsc(); }
    @Transactional(readOnly = true) public List<MessPreference> preferences() { return preferences.findAllByOrderByIdAsc(); }
    @Transactional(readOnly = true) public List<Fee> fees() { return fees.findAllByOrderByDueDateDesc(); }
    @Transactional(readOnly = true) public List<Fee> feesFor(String username) { return fees.findByStudent_IdOrderByDueDateDesc(studentForUsername(username).getId()); }
    @Transactional(readOnly = true) public List<Notification> notificationsFor(String username) { return notifications.findByStudent_IdOrderByCreatedAtDesc(studentForUsername(username).getId()); }
    @Transactional(readOnly = true) public MessPreference preferenceFor(String username) { return preferences.findByStudent_Id(studentForUsername(username).getId()).orElse(null); }
    @Transactional(readOnly = true) public List<Student> roommatesFor(String username) {
        Student student = studentForUsername(username);
        return student.getRoom() == null ? List.of() : students.findByRoom_Id(student.getRoom().getId()).stream().filter(s -> !s.getId().equals(student.getId())).toList();
    }

    @Transactional
    public RoomRequest requestRoom(String username, RoomRequestForm form) {
        Student student = studentForUsername(username);
        if (student.getRoom() != null) throw new IllegalStateException("You already have a room allocation.");
        Room room = rooms.findById(form.getRoomId()).orElseThrow(() -> new IllegalArgumentException("Room was not found."));
        if (!"AVAILABLE".equals(room.getStatus()) || room.getAvailableBeds() < 1) {
            throw new IllegalStateException("Room is already full or unavailable.");
        }
        if (requests.existsByStudent_IdAndRoom_IdAndStatus(student.getId(), room.getId(), "PENDING")) {
            throw new IllegalStateException("You already have a pending request for this room.");
        }
        RoomRequest request = new RoomRequest();
        request.setStudent(student);
        request.setRoom(room);
        request.setReason(form.getReason());
        return requests.save(request);
    }

    @Transactional
    public void reviewRoomRequest(Long requestId, ReviewForm form) {
        RoomRequest request = requests.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Room request not found."));
        if (!"PENDING".equals(request.getStatus())) throw new IllegalStateException("This request has already been reviewed.");
        if ("APPROVED".equals(form.getStatus())) {
            Student student = request.getStudent();
            Room room = rooms.findById(request.getRoom().getId()).orElseThrow(() -> new IllegalArgumentException("Room not found."));
            if (student.getRoom() != null) throw new IllegalStateException("Student already has a room allocation.");
            if (!"AVAILABLE".equals(room.getStatus()) || room.getAvailableBeds() < 1) throw new IllegalStateException("Room is already full.");
            student.setRoom(room);
            students.save(student);
            room.setOccupied(room.getOccupied() + 1);
            if (room.getAvailableBeds() == 0) room.setStatus("FULL");
            rooms.save(room);
        } else if (!"REJECTED".equals(form.getStatus())) {
            throw new IllegalArgumentException("Request status must be APPROVED or REJECTED.");
        }
        request.setStatus(form.getStatus());
        request.setAdminRemarks(form.getRemarks());
        requests.save(request);
        notify(request.getStudent(), "Room request " + form.getStatus().toLowerCase(),
                "Your request for room " + request.getRoom().getRoomNumber() + " was " + form.getStatus().toLowerCase() + ".");
    }

    @Transactional
    public Complaint submitComplaint(String username, ComplaintForm form) {
        Complaint complaint = new Complaint();
        complaint.setStudent(studentForUsername(username));
        complaint.setCategory(form.getCategory());
        complaint.setSubject(form.getSubject());
        complaint.setDescription(form.getDescription());
        return complaints.save(complaint);
    }

    @Transactional
    public void updateComplaint(Long id, ReviewForm form) {
        Complaint complaint = complaints.findById(id).orElseThrow(() -> new IllegalArgumentException("Complaint not found."));
        if (!List.of("PENDING", "IN_PROGRESS", "RESOLVED", "REJECTED").contains(form.getStatus())) throw new IllegalArgumentException("Invalid complaint status.");
        complaint.setStatus(form.getStatus());
        complaint.setAdminRemarks(form.getRemarks());
        complaints.save(complaint);
        notify(complaint.getStudent(), "Complaint update", "Complaint #" + id + " status: " + form.getStatus() + ".");
    }

    @Transactional
    public LeaveRequest applyLeave(String username, LeaveForm form) {
        if (form.getToDate().isBefore(form.getFromDate()) || form.getFromDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Leave dates must be valid and cannot be in the past.");
        }
        LeaveRequest request = new LeaveRequest();
        request.setStudent(studentForUsername(username));
        request.setFromDate(form.getFromDate());
        request.setToDate(form.getToDate());
        request.setReason(form.getReason());
        request.setParentName(form.getParentName());
        request.setParentPhone(form.getParentPhone());
        return leaves.save(request);
    }

    @Transactional
    public void reviewLeave(Long id, ReviewForm form) {
        LeaveRequest request = leaves.findById(id).orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        if (!List.of("APPROVED", "REJECTED").contains(form.getStatus())) throw new IllegalArgumentException("Leave status must be APPROVED or REJECTED.");
        request.setStatus(form.getStatus());
        request.setAdminRemarks(form.getRemarks());
        leaves.save(request);
        notify(request.getStudent(), "Leave request update", "Your leave request was " + form.getStatus().toLowerCase() + ".");
    }

    @Transactional
    public MessPreference savePreference(String username, MessForm form) {
        if (!List.of("VEGETARIAN", "NON-VEGETARIAN", "VEGAN").contains(form.getFoodPreference())) throw new IllegalArgumentException("Select a valid food preference.");
        Student student = studentForUsername(username);
        MessPreference preference = preferences.findByStudent_Id(student.getId()).orElseGet(MessPreference::new);
        preference.setStudent(student);
        preference.setFoodPreference(form.getFoodPreference());
        preference.setBreakfast(form.isBreakfast());
        preference.setLunch(form.isLunch());
        preference.setSnacks(form.isSnacks());
        preference.setDinner(form.isDinner());
        return preferences.save(preference);
    }

    @Transactional
    public MessMenu saveMenu(Long id, MenuForm form) {
        MessMenu menu = id == null ? new MessMenu() : menus.findById(id).orElseThrow(() -> new IllegalArgumentException("Menu not found."));
        menu.setMenuDate(form.getMenuDate());
        menu.setBreakfast(form.getBreakfast());
        menu.setLunch(form.getLunch());
        menu.setSnacks(form.getSnacks());
        menu.setDinner(form.getDinner());
        return menus.save(menu);
    }
    @Transactional public void deleteMenu(Long id) { menus.deleteById(id); }

    @Transactional
    public Fee addFee(FeeForm form) {
        if (form.getPaidAmount().compareTo(form.getAmount()) > 0) throw new IllegalArgumentException("Paid amount cannot exceed total fee.");
        Fee fee = new Fee();
        fee.setStudent(students.findById(form.getStudentId()).orElseThrow(() -> new IllegalArgumentException("Student not found.")));
        fee.setAmount(form.getAmount());
        fee.setPaidAmount(form.getPaidAmount());
        fee.setDueDate(form.getDueDate());
        fee.setStatus(feeStatus(form.getAmount(), form.getPaidAmount(), form.getDueDate()));
        Fee saved = fees.save(fee);
        notify(fee.getStudent(), "Hostel fee added", "A hostel fee has been added with due date " + form.getDueDate() + ".");
        return saved;
    }

    @Transactional
    public void updateFeePayment(Long id, BigDecimal paidAmount) {
        Fee fee = fees.findById(id).orElseThrow(() -> new IllegalArgumentException("Fee not found."));
        if (paidAmount == null || paidAmount.signum() < 0 || paidAmount.compareTo(fee.getAmount()) > 0) throw new IllegalArgumentException("Paid amount must be between zero and the total fee.");
        fee.setPaidAmount(paidAmount);
        fee.setStatus(feeStatus(fee.getAmount(), paidAmount, fee.getDueDate()));
        fees.save(fee);
    }

    @Transactional
    public void markNotificationRead(String username, Long id) {
        Student student = studentForUsername(username);
        Notification notification = notifications.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found."));
        if (!notification.getStudent().getId().equals(student.getId())) throw new IllegalArgumentException("Notification not found.");
        notification.setRead(true);
        notifications.save(notification);
    }

    @Transactional
    public void changePassword(String username, PasswordForm form) {
        UserAccount user = users.findByUsernameIgnoreCase(username).orElseThrow(() -> new IllegalArgumentException("Account not found."));
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) throw new IllegalArgumentException("Current password is incorrect.");
        if (!form.getNewPassword().equals(form.getConfirmPassword())) throw new IllegalArgumentException("New passwords do not match.");
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        users.save(user);
    }

    @Transactional
    public void updateProfile(String username, ProfileForm form) {
        Student student = studentForUsername(username);
        if (students.existsByEmailIgnoreCase(form.getEmail().trim())
                && !form.getEmail().equalsIgnoreCase(student.getEmail())) {
            throw new IllegalArgumentException("Email address is already in use.");
        }
        student.setName(form.getName().trim());
        student.setEmail(form.getEmail().trim().toLowerCase());
        student.setPhone(form.getPhone().trim());
        student.setDepartmentName(form.getDepartmentName().trim());
        student.setAcademicYear(form.getAcademicYear().trim());
        student.setGender(form.getGender());
        student.setHomeAddress(form.getHomeAddress().trim());
        student.setParentName(form.getParentName().trim());
        student.setParentPhone(form.getParentPhone().trim());
        students.save(student);
    }

    @Transactional
    public Hostel saveHostel(Long id, HostelForm form) {
        Hostel hostel = id == null ? new Hostel() : hostels.findById(id).orElseThrow(() -> new IllegalArgumentException("Hostel not found."));
        hostel.setName(form.getName().trim());
        hostel.setLocation(form.getLocation());
        hostel.setDescription(form.getDescription());
        hostel.setStatus(form.getStatus());
        return hostels.save(hostel);
    }

    @Transactional
    public Room saveRoom(Long id, RoomForm form) {
        Room room = id == null ? new Room() : rooms.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found."));
        if (form.getCapacity() < room.getOccupied()) throw new IllegalArgumentException("Capacity cannot be lower than the current occupancy.");
        Hostel hostel = hostels.findById(form.getHostelId()).orElseThrow(() -> new IllegalArgumentException("Hostel not found."));
        boolean duplicate = rooms.existsByHostel_IdAndRoomNumberIgnoreCase(hostel.getId(), form.getRoomNumber().trim())
                && (room.getId() == null || !room.getHostel().getId().equals(hostel.getId())
                || !room.getRoomNumber().equalsIgnoreCase(form.getRoomNumber().trim()));
        if (duplicate) throw new IllegalArgumentException("That room number already exists in this hostel.");
        if (!List.of("AVAILABLE", "FULL", "MAINTENANCE").contains(form.getStatus())) {
            throw new IllegalArgumentException("Select a valid room status.");
        }
        room.setHostel(hostel);
        room.setRoomNumber(form.getRoomNumber().trim());
        room.setCapacity(form.getCapacity());
        room.setRoomType(form.getRoomType());
        room.setStatus(room.getOccupied() >= form.getCapacity() ? "FULL" : form.getStatus());
        return rooms.save(room);
    }

    @Transactional
    public void deactivateRoom(Long id) {
        Room room = rooms.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found."));
        room.setStatus("MAINTENANCE");
        rooms.save(room);
    }

    @Transactional
    public void deactivateStudent(Long id) {
        Student student = students.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found."));
        if (student.getUserAccount() != null) {
            student.getUserAccount().setEnabled(false);
            users.save(student.getUserAccount());
        }
    }

    @Transactional
    public void updateStudentByAdmin(Long id, ProfileForm form) {
        Student student = students.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found."));
        if (students.existsByEmailIgnoreCase(form.getEmail().trim())
                && !form.getEmail().equalsIgnoreCase(student.getEmail())) {
            throw new IllegalArgumentException("Email address is already in use.");
        }
        student.setName(form.getName().trim());
        student.setEmail(form.getEmail().trim().toLowerCase());
        student.setPhone(form.getPhone().trim());
        student.setDepartmentName(form.getDepartmentName().trim());
        student.setAcademicYear(form.getAcademicYear().trim());
        student.setGender(form.getGender());
        student.setHomeAddress(form.getHomeAddress().trim());
        student.setParentName(form.getParentName().trim());
        student.setParentPhone(form.getParentPhone().trim());
        students.save(student);
    }

    @Transactional
    public void deactivateHostel(Long id) {
        Hostel hostel = hostels.findById(id).orElseThrow(() -> new IllegalArgumentException("Hostel not found."));
        hostel.setStatus("INACTIVE");
        hostels.save(hostel);
    }

    @Transactional
    public void broadcast(String title, String message) {
        students.findAll().forEach(student -> notify(student, title, message));
    }

    @Transactional
    public void createNotification(Long studentId, String title, String message) {
        Student student = students.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found."));
        notify(student, title, message);
    }

    @Transactional(readOnly = true)
    public long countStudents() { return students.count(); }
    @Transactional(readOnly = true) public long countHostels() { return hostels.count(); }
    @Transactional(readOnly = true) public long countRooms() { return rooms.count(); }
    @Transactional(readOnly = true) public long countAvailableRooms() { return rooms.findByStatusAndHostel_Status("AVAILABLE", "ACTIVE").stream().filter(r -> r.getAvailableBeds() > 0).count(); }
    @Transactional(readOnly = true) public long countFullRooms() { return rooms.countByStatus("FULL"); }
    @Transactional(readOnly = true) public long countPendingRequests() { return requests.countByStatus("PENDING"); }
    @Transactional(readOnly = true) public long countPendingComplaints() { return complaints.countByStatusIn(List.of("PENDING", "IN_PROGRESS")); }
    @Transactional(readOnly = true) public long countPendingLeaves() { return leaves.countByStatus("PENDING"); }
    @Transactional(readOnly = true) public long countPendingFees() { return fees.countByStatusIn(List.of("PENDING", "PARTIAL", "OVERDUE")); }
    @Transactional(readOnly = true) public long countPendingComplaints(String username) { return complaints.findByStudent_IdOrderByCreatedAtDesc(studentForUsername(username).getId()).stream().filter(c -> List.of("PENDING", "IN_PROGRESS").contains(c.getStatus())).count(); }
    @Transactional(readOnly = true) public long countPendingLeaves(String username) { return leaves.findByStudent_IdOrderByCreatedAtDesc(studentForUsername(username).getId()).stream().filter(l -> "PENDING".equals(l.getStatus())).count(); }
    @Transactional(readOnly = true) public long countPendingFees(String username) { return fees.findByStudent_IdOrderByDueDateDesc(studentForUsername(username).getId()).stream().filter(f -> !List.of("PAID").contains(f.getStatus())).count(); }
    @Transactional(readOnly = true) public long countPreference(String preference) { return preferences.countByFoodPreferenceIgnoreCase(preference); }
    @Transactional(readOnly = true) public List<RoomRequest> recentRequests() { return requests.findAllByOrderByRequestDateDesc().stream().limit(5).toList(); }
    @Transactional(readOnly = true) public List<Complaint> recentComplaints() { return complaints.findAllByOrderByCreatedAtDesc().stream().limit(5).toList(); }
    @Transactional(readOnly = true) public List<LeaveRequest> recentLeaves() { return leaves.findAllByOrderByCreatedAtDesc().stream().limit(5).toList(); }
    @Transactional(readOnly = true) public List<Student> allocatedStudents() { return students.findAll().stream().filter(s -> s.getRoom() != null).toList(); }

    private void notify(Student student, String title, String message) {
        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setTitle(title);
        notification.setMessage(message);
        notifications.save(notification);
    }

    private String feeStatus(BigDecimal total, BigDecimal paid, LocalDate due) {
        if (paid.compareTo(total) >= 0) return "PAID";
        if (paid.signum() > 0) return "PARTIAL";
        return due.isBefore(LocalDate.now()) ? "OVERDUE" : "PENDING";
    }
}
