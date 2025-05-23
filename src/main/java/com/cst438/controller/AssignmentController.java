package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {
    
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired	
    private SectionRepository sectionRepository;
		
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
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO dto, Principal principal) {
        Section section = sectionRepository.findById(dto.secNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found."));
        if(!section.getInstructorEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to create assignments for this section.");
        }
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
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto,Principal principal) {

	Assignment assignment = assignmentRepository.findById(dto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found."));
        if(!assignment.getSection().getInstructorEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update assignments for this section.");
        }
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
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId,Principal principal) {
	if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found.");
        }
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found."));

        if(!assignment.getSection().getInstructorEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete assignments for this section.");
        }

        assignmentRepository.deleteById(assignmentId);
    }
}
