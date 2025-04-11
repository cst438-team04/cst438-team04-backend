package com.cst438.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class GradeRepositoryTest {
    @Autowired
    private GradeRepository gradeRepository;

    @Test public void testFindByEnrollmentIdAndAssignmentId() {
        Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(2, 1);
        assertNotNull(grade);
        assertEquals(95, grade.getScore());
    }
}
