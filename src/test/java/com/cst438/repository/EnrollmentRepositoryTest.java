package com.cst438.repository;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test void testFindEnrollmentsBySectionNoOrderByStudentName() {
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(1);
        assertNotNull(enrollments);
        assertTrue(enrollments.size() > 0);
        assertEquals("thomas edison",enrollments.get(0).getUser().getName());
    }

    @Test void testfindEnrollmentsByStudentIdOrderByTermId() {
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(3);
        assertNotNull(enrollments);
        assertTrue(enrollments.size() > 0);
        assertEquals("thomas edison",enrollments.get(0).getUser().getName());
    }

    @Test void testfindByYearAndSemesterOrderByCourseId() {
        List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(2024,"Fall",3);
        assertNotNull(enrollments);
        assertTrue(enrollments.size() > 0);
        assertEquals("thomas edison",enrollments.get(0).getUser().getName());
        assertEquals("cst338",enrollments.get(0).getSection().getCourse().getCourseId());
    }

    @Test void testfindEnrollmentBySectionNoAndStudentId() {
        Enrollment enrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(1,3);
        assertNotNull(enrollment);
        assertEquals("thomas edison",enrollment.getUser().getName());
        assertEquals("cst338",enrollment.getSection().getCourse().getCourseId());

    }

}
