package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Section;
import com.cst438.domain.SectionRepository;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.SectionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class EnrollmentControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    public void enrollSection() throws Exception {

        MockHttpServletResponse response;
        //create DTo with new enrollment
                EnrollmentDTO enrollment = new EnrollmentDTO(
                        20,
                        null,
                        3,
                        "Thomas Edison",
                        "tedison@csumb.edu",
                        "cst363",
                        "Introduction to Database",
                        3,
                        3,
                        "052",
                        "104",
                        "M W 10:00-11:50",
                        4,
                        2025,
                        "Spring"
        );
        String currentDate = "2024-06-30";
        // issue an http Post request to SpringTestServer
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", enrollment.sectionNo())
                                .param("studentId", "3")
                                .param("date", currentDate)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        assertEquals(200,response.getStatus());

        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        assertNotEquals(0,result.enrollmentId());
        assertEquals("052",result.building());

        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);
        assertEquals(3,e.getSection().getSectionNo());

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/enrollments/"+result.enrollmentId()))
                .andReturn()
                .getResponse();
        assertEquals(200, response.getStatus());

        e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNull(e);
    }

    //this test makes sure a student can't enroll in a section that they are already enrolled in.
    @Test
    public void enrollSectionAgain() throws Exception {

        MockHttpServletResponse response;
        //create DTo with new enrollment
        EnrollmentDTO enrollment = new EnrollmentDTO(
                25,
                "A",
                3,
                "Thomas Edison",
                "tedison@csumb.edu",
                "cst363",
                "Introduction to Database",
                1,
                8,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2025,
                "Spring"
        );
        String currentDate = "2024-06-30";
        // issue an http Post request to SpringTestServer
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", enrollment.sectionNo())
                                .param("studentId", "3")
                                .param("date", currentDate)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        assertEquals(400,response.getStatus());

        assertEquals("Student is already enrolled in this section", response.getContentAsString());
    }

    @Test
    public void enrollSectionBadSectionNo() throws Exception {
        //need to handle section handling come back to

        MockHttpServletResponse response;
        //create DTo with new enrollment
        EnrollmentDTO enrollment = new EnrollmentDTO(
                25,
                "A",
                3,
                "Thomas Edison",
                "tedison@csumb.edu",
                "cst363",
                "Introduction to Database",
                1,
                100,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2025,
                "Spring"
        );
        String currentDate = "2024-06-30";

        // issue an http Post request to SpringTestServer
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", enrollment.sectionNo())
                                .param("studentId", "3")
                                .param("date", currentDate)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        assertEquals(400,response.getStatus());

        assertEquals("Section Not Found", response.getContentAsString());
    }

    @Test
    public void enrollSectionPastDate() throws Exception {


        MockHttpServletResponse response;
        //create DTo with new enrollment
        EnrollmentDTO enrollment = new EnrollmentDTO(
                20,
                null,
                3,
                "Thomas Edison",
                "tedison@csumb.edu",
                "cst363",
                "Introduction to Database",
                3,
                3,
                "052",
                "104",
                "M W 10:00-11:50",
                4,
                2025,
                "Spring"
        );

        String currentDate = "2025-06-30";

        // issue an http Post request to SpringTestServer
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", enrollment.sectionNo())
                                .param("studentId", "3")
                                .param("date", currentDate)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                .andReturn()
                .getResponse();

        assertEquals(400,response.getStatus());

        assertEquals("Cannot enroll. The add deadline has passed for this section", response.getContentAsString());
    }

    @Test
    public void updateEnrollmentGrade() throws Exception
    {
        MockHttpServletResponse response;


        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/sections/{sectionNo}/enrollments", 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(200,response.getStatus());
        List<EnrollmentDTO> enrollments = Arrays.asList(fromJsonString(response.getContentAsString(), EnrollmentDTO[].class));

        assertEquals("thomas edison",enrollments.get(0).name());
        assertEquals("A", enrollments.get(0).grade());
        assertEquals("tedison@csumb.edu",enrollments.get(0).email());


        List<EnrollmentDTO> updatedEnrollments = new ArrayList<EnrollmentDTO>();

        for(EnrollmentDTO enrollmentToUpdate : enrollments)
        {
            EnrollmentDTO  updatedEnrollment = new EnrollmentDTO(
                    enrollmentToUpdate.enrollmentId(),
                    "D",
                    enrollmentToUpdate.studentId(),
                    enrollmentToUpdate.name(),
                    enrollmentToUpdate.email(),
                    enrollmentToUpdate.courseId(),
                    enrollmentToUpdate.title(),
                    enrollmentToUpdate.sectionId(),
                    enrollmentToUpdate.sectionNo(),
                    enrollmentToUpdate.building(),
                    enrollmentToUpdate.room(),
                    enrollmentToUpdate.times(),
                    enrollmentToUpdate.credits(),
                    enrollmentToUpdate.year(),
                    enrollmentToUpdate.semester()
            );

            updatedEnrollments.add(updatedEnrollment);
        }


        String updatedEnrollmentJson = asJsonString(updatedEnrollments);

        MockHttpServletResponse updateResponse = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments", updatedEnrollments)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedEnrollmentJson))
                .andReturn()
                .getResponse();

        assertEquals(200,updateResponse.getStatus());

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/sections/{sectionNo}/enrollments", 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();



        assertEquals(200,response.getStatus());
        enrollments = Arrays.asList(fromJsonString(response.getContentAsString(), EnrollmentDTO[].class));

        assertEquals("thomas edison",enrollments.get(0).name());
        assertEquals("D", enrollments.get(0).grade());
        assertEquals("tedison@csumb.edu",enrollments.get(0).email());
    }
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
