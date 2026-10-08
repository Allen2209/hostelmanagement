package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.PortalForms.ReviewForm;
import com.example.hostelmanagement.model.*;
import com.example.hostelmanagement.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortalServiceTest {
    @Mock private StudentRepository students;
    @Mock private UserAccountRepository users;
    @Mock private HostelRepository hostels;
    @Mock private RoomRepository rooms;
    @Mock private RoomRequestRepository requests;
    @Mock private ComplaintRepository complaints;
    @Mock private LeaveRequestRepository leaves;
    @Mock private MessPreferenceRepository preferences;
    @Mock private MessMenuRepository menus;
    @Mock private FeeRepository fees;
    @Mock private NotificationRepository notifications;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private PortalService service;

    @Test
    void approvingRequestAllocatesBedAndMarksRoomFullAtCapacity() {
        Student student = new Student("HOSTEL000001", "Alex", null, "Campus", "Science");
        student.setId(1L);
        Room room = new Room();
        room.setId(4L);
        room.setCapacity(2);
        room.setOccupied(1);
        room.setStatus("AVAILABLE");
        RoomRequest request = new RoomRequest();
        request.setId(9L);
        request.setStudent(student);
        request.setRoom(room);
        when(requests.findById(9L)).thenReturn(Optional.of(request));
        when(rooms.findById(4L)).thenReturn(Optional.of(room));

        ReviewForm review = new ReviewForm();
        review.setStatus("APPROVED");
        review.setRemarks("Approved");
        service.reviewRoomRequest(9L, review);

        assertSame(room, student.getRoom());
        assertEquals(2, room.getOccupied());
        assertEquals("FULL", room.getStatus());
        assertEquals("APPROVED", request.getStatus());
        verify(students).save(student);
        verify(rooms).save(room);
        verify(requests).save(request);
        verify(notifications).save(any(Notification.class));
    }

    @Test
    void refusesRoomRequestWhenStudentAlreadyHasRoom() {
        Student student = new Student("HOSTEL000001", "Alex", null, "Campus", "Science");
        student.setId(1L);
        student.setRoom(new Room());
        when(students.findByStudentIdIgnoreCase("STU001")).thenReturn(Optional.of(student));
        com.example.hostelmanagement.dto.PortalForms.RoomRequestForm form =
                new com.example.hostelmanagement.dto.PortalForms.RoomRequestForm();
        form.setRoomId(4L);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.requestRoom("STU001", form));

        assertTrue(error.getMessage().contains("already have a room"));
        verifyNoInteractions(rooms);
        verifyNoInteractions(requests);
    }
}
