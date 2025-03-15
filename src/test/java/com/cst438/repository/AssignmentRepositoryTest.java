package com.cst438.repository;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class AssignmentRepositoryTest {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Test void testFindBySectionNoOrderByDueDate() {
        List <Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(8);
        assertNotNull(assignments);
        assertTrue(assignments.size() > 0);
        assertEquals("db homework 1", assignments.get(0).getTitle());
    }

    @Test void testfindByStudentIdAndYearAndSemesterOrderByDueDate()
    {
        List <Assignment> assignments = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(3,2025,"Spring");
        assertNotNull(assignments);
        assertTrue(assignments.size() > 0);
        assertEquals("thomas edison", assignments.get(0).getSection().getEnrollments().get(0).getUser().getName());
        assertEquals("db homework 1", assignments.get(0).getTitle());
    }

}
