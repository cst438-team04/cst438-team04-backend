package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.GradeRepository;
import com.cst438.dto.GradeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class GradeController {

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SectionRepository sectionRepository;


    /**
     instructor lists the grades for an assignment for all enrolled students
     returns the list of grades (ordered by student name) for the assignment
     if there is no grade entity for an enrolled student, a grade entity with null grade is created
     logged in user must be the instructor for the section (assignment 7)
     */
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        Section section = assignment.getSection();

        List<Enrollment> enrollments =  enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(section.getSectionNo());
        List<GradeDTO> grade_dto_list = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollment.getEnrollmentId(), assignmentId);
            if(grade==null){
                grade = new Grade();
                grade.setEnrollment(enrollment);
                grade.setAssignment(assignment);
                grade.setScore(null);
                gradeRepository.save(grade);
            }
            grade_dto_list.add(new GradeDTO(grade.getGradeId(),enrollment.getUser().getName(),enrollment.getUser().getEmail(),grade.getAssignment().getTitle(),enrollment.getSection().getCourse().getCourseId(),enrollment.getSection().getSectionNo(),grade.getScore()));
        }

        // TODO remove the following line when done


        return grade_dto_list;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    /**
     instructor updates one or more assignment grades
     only the score attribute of grade entity can be changed
     logged in user must be the instructor for the section (assignment 7)
     */
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {

        // TODO
        for (GradeDTO grade : dlist) {
            Grade g = gradeRepository.findById(grade.gradeId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"+grade.gradeId()));
            g.setScore(grade.score());
            gradeRepository.save(g);
        }
        // for each grade in the GradeDTO list, retrieve the grade entity
        // update the score and save the entity

    }

}
