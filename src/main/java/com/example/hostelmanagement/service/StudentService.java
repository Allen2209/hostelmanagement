package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.RegisterRequest;
import com.example.hostelmanagement.exception.StudentAlreadyExistsException;
import com.example.hostelmanagement.model.Student;
import com.example.hostelmanagement.repository.StudentRepository;
import com.example.hostelmanagement.repository.UserAccountRepository;
import com.example.hostelmanagement.model.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern ACCOUNT_NUM_PATTERN = Pattern.compile("HOSTEL(\\d+)");
    private static final DateTimeFormatter[] ACCEPTED_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    public StudentService(StudentRepository studentRepository, UserAccountRepository userAccountRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Parses date of birth string into LocalDate.
     * Prefers DD-MM-YYYY, but supports common delimiters and ISO format gracefully.
     */
    public LocalDate parseDateOfBirth(String dobString) {
        if (dobString == null || dobString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date of Birth cannot be empty");
        }
        String trimmed = dobString.trim();
        for (DateTimeFormatter formatter : ACCEPTED_FORMATS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Invalid Date of Birth format. Please enter as DD-MM-YYYY (e.g., 15-08-2003)");
    }

    /**
     * Generates a unique sequential hostel account number.
     * Format: HOSTEL000001, HOSTEL000002, etc.
     */
    @Transactional
    public synchronized String generateNextAccountNumber() {
        String highestAccount = studentRepository.findHighestAccountNumber();
        long nextIndex = 1L;

        if (highestAccount != null && !highestAccount.trim().isEmpty()) {
            Matcher matcher = ACCOUNT_NUM_PATTERN.matcher(highestAccount.trim());
            if (matcher.find()) {
                try {
                    nextIndex = Long.parseLong(matcher.group(1)) + 1;
                } catch (NumberFormatException e) {
                    nextIndex = studentRepository.count() + 1;
                }
            }
        }

        String candidate = String.format("HOSTEL%06d", nextIndex);
        // Ensure uniqueness even in case of gaps or manual entries
        while (studentRepository.existsByAccountNumber(candidate)) {
            nextIndex++;
            candidate = String.format("HOSTEL%06d", nextIndex);
        }

        return candidate;
    }

    /**
     * Registers a new student and generates their account number.
     */
    @Transactional
    public Student registerStudent(RegisterRequest request) {
        LocalDate dob = parseDateOfBirth(request.getDateOfBirth());
        String cleanName = request.getName().trim();

        if (studentRepository.existsByStudentIdIgnoreCase(request.getStudentId().trim())
                || userAccountRepository.existsByUsernameIgnoreCase(request.getStudentId().trim())) {
            throw new IllegalArgumentException("Student ID is already registered.");
        }
        if (studentRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        Optional<Student> existing = studentRepository.findByNameIgnoreCaseAndDateOfBirth(cleanName, dob);
        if (existing.isPresent()) {
            throw new StudentAlreadyExistsException(
                    "A resident with name '" + cleanName + "' and Date of Birth '" +
                            dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            "' is already registered.",
                    existing.get().getAccountNumber()
            );
        }

        String accountNumber = generateNextAccountNumber();

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request.getStudentId().trim());
        userAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        userAccount.setRole("STUDENT");
        userAccount.setEnabled(true);
        userAccountRepository.save(userAccount);

        Student student = new Student(
                accountNumber,
                cleanName,
                dob,
                request.getHomeAddress().trim(),
                request.getDepartmentName().trim()
        );
        student.setStudentId(request.getStudentId().trim());
        student.setEmail(request.getEmail().trim().toLowerCase());
        student.setPhone(request.getPhone().trim());
        student.setAcademicYear(request.getAcademicYear().trim());
        student.setGender(request.getGender().trim());
        student.setParentName(request.getParentName().trim());
        student.setParentPhone(request.getParentPhone().trim());
        student.setUserAccount(userAccount);

        return studentRepository.save(student);
    }

    /**
     * Authenticates a student using Name and Date of Birth.
     */
    public Optional<Student> authenticate(String name, String dobString) {
        if (name == null || name.trim().isEmpty() || dobString == null || dobString.trim().isEmpty()) {
            return Optional.empty();
        }

        LocalDate dob;
        try {
            dob = parseDateOfBirth(dobString);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        return studentRepository.findByNameIgnoreCaseAndDateOfBirth(name.trim(), dob);
    }

    /**
     * Retrieves student by account number.
     */
    public Optional<Student> findByAccountNumber(String accountNumber) {
        return studentRepository.findByAccountNumber(accountNumber);
    }

    public Optional<Student> findByStudentId(String studentId) {
        return studentRepository.findByStudentIdIgnoreCase(studentId);
    }
}
