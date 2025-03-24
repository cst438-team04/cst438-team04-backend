package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.Section;
import com.cst438.domain.User;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.SectionRepository;
import com.cst438.domain.UserRepository;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentScheduleController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Students list their transcript containing all enrollments.
     * Returns a list of enrollments in chronological order.
     * Logged-in user must be the student.
     * Example URL: /transcripts?studentId=19803
     */
    @GetMapping("/transcripts")
    public ResponseEntity<?> getTranscript(@RequestParam("studentId") String studentId) {
        try {
            int id = Integer.parseInt(studentId);
            List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(id);

            List<EnrollmentDTO> enrollmentDTOs = enrollments.stream()
                    .map(enrollment -> new EnrollmentDTO(
                            enrollment.getEnrollmentId(),
                            enrollment.getGrade(),
                            enrollment.getUser().getId(),
                            enrollment.getUser().getName(),
                            enrollment.getUser().getEmail(),
                            enrollment.getSection().getCourse().getCourseId(),
                            enrollment.getSection().getCourse().getTitle(),
                            enrollment.getSection().getSecId(),
                            enrollment.getSection().getSectionNo(),
                            enrollment.getSection().getBuilding(),
                            enrollment.getSection().getRoom(),
                            enrollment.getSection().getTimes(),
                            enrollment.getSection().getCourse().getCredits(),
                            enrollment.getSection().getTerm().getYear(),
                            enrollment.getSection().getTerm().getSemester()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(enrollmentDTOs);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid studentId: " + studentId);
        }
    }

    /**
     * Students enroll into a section of a course.
     * Returns the enrollment data including the primary key.
     * Logged-in user must be the student.
     */
    @PostMapping("/enrollments/sections/{sectionNo}")
    public ResponseEntity<?> addCourse(
            @PathVariable int sectionNo,
            @RequestParam("studentId") String studentId) {

        try {
            int id = Integer.parseInt(studentId);

            Section section = sectionRepository.findById(sectionNo)
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            User student = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Check if the student is already enrolled in the section
            Enrollment existingEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionNo, id);
            if (existingEnrollment != null) {
                return ResponseEntity.badRequest().body("Student is already enrolled in this section");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setUser(student);
            enrollment.setSection(section);
            enrollment.setGrade(null);

            Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

            EnrollmentDTO enrollmentDTO = new EnrollmentDTO(
                    savedEnrollment.getEnrollmentId(),
                    savedEnrollment.getGrade(),
                    savedEnrollment.getUser().getId(),
                    savedEnrollment.getUser().getName(),
                    savedEnrollment.getUser().getEmail(),
                    savedEnrollment.getSection().getCourse().getCourseId(),
                    savedEnrollment.getSection().getCourse().getTitle(),
                    savedEnrollment.getSection().getSecId(),
                    savedEnrollment.getSection().getSectionNo(),
                    savedEnrollment.getSection().getBuilding(),
                    savedEnrollment.getSection().getRoom(),
                    savedEnrollment.getSection().getTimes(),
                    savedEnrollment.getSection().getCourse().getCredits(),
                    savedEnrollment.getSection().getTerm().getYear(),
                    savedEnrollment.getSection().getTerm().getSemester()
            );

            return ResponseEntity.ok(enrollmentDTO);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid studentId: " + studentId);
        }
    }

    /**
     * Students drop an enrollment for a section.
     * Logged-in user must be the student.
     */
    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<?> dropCourse(@PathVariable("enrollmentId") String enrollmentId) {
        try {
            int id = Integer.parseInt(enrollmentId);

            Enrollment enrollment = enrollmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));

            enrollmentRepository.delete(enrollment);

            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid enrollmentId: " + enrollmentId);
        }
    }
}
