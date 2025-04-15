package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
public class GradeControllerTest {

   /* @Autowired
    MockMvc mvc;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    GradeRepository gradeRepository;

    @Test
    public void gradeAssignment() throws Exception {

        MockHttpServletResponse response;

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/assignments/{assignmentId}/grades", 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(200,response.getStatus());
        List<GradeDTO> grades = Arrays.asList(fromJsonString(response.getContentAsString(), GradeDTO[].class));

        assertEquals(95,grades.get(0).score());
        assertEquals("db homework 1",grades.get(0).assignmentTitle());

        List<GradeDTO> updatedGrades = new ArrayList<GradeDTO>();

        for(GradeDTO gradeToUpdate: grades) {
            GradeDTO updatedGrade = new GradeDTO(
                    gradeToUpdate.gradeId(),
                    gradeToUpdate.studentName(),
                    gradeToUpdate.studentEmail(),
                    gradeToUpdate.assignmentTitle(),
                    gradeToUpdate.courseId(),
                    gradeToUpdate.sectionId(),
                    40
            );
            updatedGrades.add(updatedGrade);
        }

        String updatedGradeJson = asJsonString(updatedGrades);

        MockHttpServletResponse updateResponse = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/grades", updatedGrades)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedGradeJson))
                .andReturn()
                .getResponse();

        assertEquals(200,updateResponse.getStatus());

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/assignments/{assignmentId}/grades", 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(200,response.getStatus());
        grades = Arrays.asList(fromJsonString(response.getContentAsString(), GradeDTO[].class));

        assertEquals(40,grades.get(0).score());
        assertEquals("db homework 1",grades.get(0).assignmentTitle());
    }

    @Test
    public void gradeInvalidAssignment() throws Exception {

        MockHttpServletResponse response;

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/assignments/{assignmentId}/grades", 20000)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(404,response.getStatus());
        assertEquals("Assignment not found",response.getErrorMessage());
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
    }*/
}
