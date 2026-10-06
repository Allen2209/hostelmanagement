package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.RegisterRequest;
import com.example.hostelmanagement.exception.StudentAlreadyExistsException;
import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should generate HOSTEL000001 when database is empty")
    void testGenerateFirstAccountNumber() {
        when(studentRepository.findHighestAccountNumber()).thenReturn(null);
        when(studentRepository.existsByAccountNumber("HOSTEL000001")).thenReturn(false);

        String accountNum = studentService.generateNextAccountNumber();
        assertEquals("HOSTEL000001", accountNum);
    }

    @Test
    @DisplayName("Should increment account number correctly from HOSTEL000005 to HOSTEL000006")
    void testGenerateNextAccountNumber() {
        when(studentRepository.findHighestAccountNumber()).thenReturn("HOSTEL000005");
        when(studentRepository.existsByAccountNumber("HOSTEL000006")).thenReturn(false);

        String accountNum = studentService.generateNextAccountNumber();
        assertEquals("HOSTEL000006", accountNum);
    }

    @Test
    @DisplayName("Should register new student with generated account number")
    void testRegisterStudentSuccess() {
        RegisterRequest request = new RegisterRequest(
                "Alex Rivera",
                "15-08-2003",
                "124 Campus Avenue, Block B",
                "Computer Science"
        );

        when(studentRepository.findByNameIgnoreCaseAndDateOfBirth(eq("Alex Rivera"), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(studentRepository.findHighestAccountNumber()).thenReturn(null);
        when(studentRepository.existsByAccountNumber("HOSTEL000001")).thenReturn(false);

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Student saved = studentService.registerStudent(request);
        assertNotNull(saved);
        assertEquals("Alex Rivera", saved.getName());
        assertEquals("HOSTEL000001", saved.getAccountNumber());
        assertEquals("Computer Science", saved.getDepartmentName());
    }

    @Test
    @DisplayName("Should reject duplicate registration with clear exception")
    void testRegisterDuplicateStudent() {
        RegisterRequest request = new RegisterRequest(
                "Alex Rivera",
                "15-08-2003",
                "124 Campus Avenue",
                "Computer Science"
        );

        Student existing = new Student("HOSTEL000001", "Alex Rivera", LocalDate.of(2003, 8, 15), "Address", "CS");
        when(studentRepository.findByNameIgnoreCaseAndDateOfBirth(eq("Alex Rivera"), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));

        assertThrows(StudentAlreadyExistsException.class, () -> studentService.registerStudent(request));
    }

    @Test
    @DisplayName("Should parse various valid Date of Birth formats")
    void testParseDateOfBirth() {
        LocalDate date1 = studentService.parseDateOfBirth("15-08-2003");
        assertEquals(LocalDate.of(2003, 8, 15), date1);

        LocalDate date2 = studentService.parseDateOfBirth("2003-08-15");
        assertEquals(LocalDate.of(2003, 8, 15), date2);

        LocalDate date3 = studentService.parseDateOfBirth("05/12/2001");
        assertEquals(LocalDate.of(2001, 12, 5), date3);
    }
}
