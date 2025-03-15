package com.cst438.repository;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Grade;
import com.cst438.domain.GradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
