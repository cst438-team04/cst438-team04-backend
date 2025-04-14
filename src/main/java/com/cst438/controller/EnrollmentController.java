package com.cst438.controller;


import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.SectionRepository;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.service.RegistrarServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    private RegistrarServiceProxy registrarService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SectionRepository sectionRepository;



    /**
     instructor gets list of enrollments for a section
     list of enrollments returned is in order by student name
     logged in user must be the instructor for the section (assignment 7)
     */
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(
            @PathVariable("sectionNo") int sectionNo ) {

        // TODO
		//  hint: use enrollment repository findEnrollmentsBySectionNoOrderByStudentName method
        //  remove the following line when done
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);

        if(enrollments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No enrollments found for section " + sectionNo);
        }

        List<EnrollmentDTO> enrollmentDTOs = new ArrayList<>();
        for(Enrollment enrollment : enrollments) {
            EnrollmentDTO dto = new EnrollmentDTO(
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
            );
            enrollmentDTOs.add(dto);
        }
        return enrollmentDTOs;
    }

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    /**
     instructor updates enrollment grades
     only the grade attribute of enrollment can be changed
     logged in user must be the instructor for the section (assignment 7)
     */
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist) {

        // TODO

        // For each EnrollmentDTO in the list
        //  find the Enrollment entity using enrollmentId
        //  update the grade and save back to database
        if (dlist == null || dlist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be empty.");
        }

        for(EnrollmentDTO enrollmentDTO : dlist) {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentDTO.enrollmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment ID " + enrollmentDTO.enrollmentId() + " not found."));

            enrollment.setGrade(enrollmentDTO.grade());

            enrollmentRepository.save(enrollment);

            registrarService.updateEnrollment(enrollmentDTO);
        }

    }
}
