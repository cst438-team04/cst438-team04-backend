package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.cst438.dto.SectionDTO;



import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {
    
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired	
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
		
    /**
     instructor lists assignments for a section.
     Assignment data is returned ordered by due date.
     logged in user must be the instructor for the section (assignment 7)
     */
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(@PathVariable("secNo") int secNo) {
	List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);

	if (assignments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No assignments found for section " + secNo);
        }

	List<AssignmentDTO> assignmentDTOs = new ArrayList<>();
        for (Assignment a : assignments) {
            assignmentDTOs.add(new AssignmentDTO(
                a.getAssignmentId(), 
                a.getTitle(), 
                a.getDueDate().toString(), 
                a.getSection().getCourse().getCourseId(), 
                a.getSection().getSecId(), 
                a.getSection().getSectionNo()
            ));
        }
        return assignmentDTOs;    
		// hint: use the assignment repository method 
		//  findBySectionNoOrderByDueDate to return 
		//  a list of assignments
    }

    /**
     instructor creates an assignment for a section.
     Assignment data with primary key is returned.
     logged in user must be the instructor for the section (assignment 7)
     */
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO dto) {
        Section section = sectionRepository.findById(dto.secNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found."));

	Assignment assignment = new Assignment();
        assignment.setTitle(dto.title());    

	try {
            assignment.setDueDate(java.sql.Date.valueOf(dto.dueDate())); // Ensure date conversion
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD.");
        }    

	assignment.setSection(section);
        assignment = assignmentRepository.save(assignment);

        return new AssignmentDTO(
            assignment.getAssignmentId(),
            assignment.getTitle(),
            assignment.getDueDate().toString(),
            section.getCourse().getCourseId(),
            section.getSecId(),
            section.getSectionNo()
        ); 
    }

    /**
     instructor updates an assignment for a section.
     only title and dueDate may be changed
     updated assignment data is returned
     logged in user must be the instructor for the section (assignment 7)
     */
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {
	Assignment assignment = assignmentRepository.findById(dto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found."));

	assignment.setTitle(dto.title());

        try {
            assignment.setDueDate(java.sql.Date.valueOf(dto.dueDate()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD.");
        }

	assignment = assignmentRepository.save(assignment);    
	    
        return new AssignmentDTO(
            assignment.getAssignmentId(),
            assignment.getTitle(),
            assignment.getDueDate().toString(),
            assignment.getSection().getCourse().getCourseId(),
            assignment.getSection().getSecId(),
            assignment.getSection().getSectionNo()
        );
    }

    /**
     instructor deletes an assignment for a section.
     logged in user must be the instructor for the section (assignment 7)
     */
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {
	if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found.");
        }
        assignmentRepository.deleteById(assignmentId);
    }

    @GetMapping("/sections")
    public List<SectionDTO> getSectionsForInstructor(
            @RequestParam("email") String instructorEmail,
            @RequestParam("year") int year ,
            @RequestParam("semester") String semester )  {


        List<Section> sections = sectionRepository.findByInstructorEmailAndYearAndSemester(instructorEmail, year, semester);

        List<SectionDTO> dto_list = new ArrayList<>();
        for (Section s : sections) {
            User instructor = null;
            if (s.getInstructorEmail()!=null) {
                instructor = userRepository.findByEmail(s.getInstructorEmail());
            }
            dto_list.add(new SectionDTO(
                    s.getSectionNo(),
                    s.getTerm().getYear(),
                    s.getTerm().getSemester(),
                    s.getCourse().getCourseId(),
                    s.getCourse().getTitle(),
                    s.getSecId(),
                    s.getBuilding(),
                    s.getRoom(),
                    s.getTimes(),
                    (instructor!=null) ? instructor.getName() : "",
                    (instructor!=null) ? instructor.getEmail() : ""
            ));
        }
        return dto_list;
    }

    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

        // TODO remove the following line when done

        // return a list of assignments and (if they exist) the assignment grade
        //  for all sections that the student is enrolled for the given year and semester
        //  hint: use the assignment repository method findByStudentIdAndYearAndSemesterOrderByDueDate
        List<Assignment> assignments = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId, year, semester);

        List<AssignmentStudentDTO> assignmentStudentDTOs = new ArrayList<>();
        for (Assignment assignment : assignments) {
            Integer score = null;

            Enrollment studentEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(assignment.getSection().getSectionNo(), studentId);

            if (studentEnrollment != null) {
                Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(studentEnrollment.getEnrollmentId(), assignment.getAssignmentId());
                if (grade != null) {
                    score = grade.getScore();
                }
            }

            AssignmentStudentDTO dto = new AssignmentStudentDTO(
                    assignment.getAssignmentId(),
                    assignment.getTitle(),
                    assignment.getDueDate(),
                    assignment.getSection().getCourse().getCourseId(),
                    assignment.getSection().getSectionNo(),
                    score
            );
            assignmentStudentDTOs.add(dto);
        }

        return assignmentStudentDTOs;
    }
}
