package com.example.hostelmanagement.config;

import com.example.hostelmanagement.model.*;
import com.example.hostelmanagement.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.math.BigDecimal;

@Configuration
public class DemoDataInitializer {
    @Bean
    ApplicationRunner seedDemoData(UserAccountRepository users, StudentRepository students,
                                   HostelRepository hostels, RoomRepository rooms,
                                   MessMenuRepository menus, ComplaintRepository complaints,
                                   LeaveRequestRepository leaves, FeeRepository fees,
                                   NotificationRepository notifications,
                                   PasswordEncoder encoder) {
        return args -> seed(users, students, hostels, rooms, menus, complaints, leaves, fees, notifications, encoder);
    }

    @Transactional
    void seed(UserAccountRepository users, StudentRepository students, HostelRepository hostels,
              RoomRepository rooms, MessMenuRepository menus, ComplaintRepository complaints,
              LeaveRequestRepository leaves, FeeRepository fees, NotificationRepository notifications,
              PasswordEncoder encoder) {
        Student demoStudent = null;
        if (users.count() == 0) {
            UserAccount admin = new UserAccount();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole("ADMIN");
            users.save(admin);

            UserAccount studentUser = new UserAccount();
            studentUser.setUsername("student");
            studentUser.setPassword(encoder.encode("student123"));
            studentUser.setRole("STUDENT");
            users.save(studentUser);

            demoStudent = new Student(
                    "HOSTEL000001", "Sample Student", LocalDate.of(2004, 5, 12),
                    "College Campus", "Computer Science"
            );
            demoStudent.setStudentId("STU001");
            demoStudent.setEmail("student@hostel.com");
            demoStudent.setPhone("9000000001");
            demoStudent.setAcademicYear("2");
            demoStudent.setGender("OTHER");
            demoStudent.setParentName("Parent");
            demoStudent.setParentPhone("9000000002");
            demoStudent.setUserAccount(studentUser);
            demoStudent = students.save(demoStudent);

            UserAccount secondUser = new UserAccount();
            secondUser.setUsername("student2");
            secondUser.setPassword(encoder.encode("student123"));
            secondUser.setRole("STUDENT");
            users.save(secondUser);
            Student secondStudent = new Student(
                    "HOSTEL000002", "Jordan Lee", LocalDate.of(2003, 7, 21),
                    "City Centre", "Business Administration"
            );
            secondStudent.setStudentId("STU002");
            secondStudent.setEmail("student2@hostel.com");
            secondStudent.setPhone("9000000003");
            secondStudent.setAcademicYear("3");
            secondStudent.setGender("OTHER");
            secondStudent.setParentName("Guardian");
            secondStudent.setParentPhone("9000000004");
            secondStudent.setUserAccount(secondUser);
            secondStudent = students.save(secondStudent);

            Complaint complaint = new Complaint();
            complaint.setStudent(secondStudent);
            complaint.setCategory("Maintenance");
            complaint.setSubject("Study lamp needs repair");
            complaint.setDescription("The study lamp in the room needs maintenance.");
            complaints.save(complaint);

            LeaveRequest leave = new LeaveRequest();
            leave.setStudent(secondStudent);
            leave.setFromDate(LocalDate.now().plusDays(7));
            leave.setToDate(LocalDate.now().plusDays(8));
            leave.setReason("Weekend family visit");
            leave.setParentName("Guardian");
            leave.setParentPhone(secondStudent.getParentPhone());
            leaves.save(leave);

            Fee fee = new Fee();
            fee.setStudent(demoStudent);
            fee.setAmount(new BigDecimal("12000.00"));
            fee.setPaidAmount(new BigDecimal("5000.00"));
            fee.setDueDate(LocalDate.now().plusDays(30));
            fee.setStatus("PARTIAL");
            fees.save(fee);

            Notification notice = new Notification();
            notice.setStudent(demoStudent);
            notice.setTitle("Welcome to HostelHub");
            notice.setMessage("Your hostel services are ready. You can browse available rooms and submit requests.");
            notifications.save(notice);
        }

        if (hostels.count() == 0) {
            Hostel hostel = new Hostel();
            hostel.setName("A Block");
            hostel.setLocation("College Campus");
            hostel.setDescription("Main student residence");
            hostel.setStatus("ACTIVE");
            hostel = hostels.save(hostel);

            for (int index = 101; index <= 104; index++) {
                Room room = new Room();
                room.setHostel(hostel);
                room.setRoomNumber(String.valueOf(index));
                room.setCapacity(4);
                room.setOccupied(0);
                room.setRoomType("4 Sharing");
                room.setStatus("AVAILABLE");
                rooms.save(room);
            }
        }
        if (menus.count() == 0) {
            MessMenu menu = new MessMenu();
            menu.setMenuDate(LocalDate.now());
            menu.setBreakfast("Idli and sambar");
            menu.setLunch("Rice, dal and vegetables");
            menu.setSnacks("Tea and biscuits");
            menu.setDinner("Chapati and dal");
            menus.save(menu);
        }
    }
}
