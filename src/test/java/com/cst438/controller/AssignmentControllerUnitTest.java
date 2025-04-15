package com.cst438.controller;

import com.cst438.domain.SectionRepository;
import com.cst438.dto.AssignmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AssignmentControllerUnitTest {
  /*  @Autowired
    MockMvc mvc;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Test
    public void addAssignment() throws Exception {
        MockHttpServletResponse response;

        AssignmentDTO dto = new AssignmentDTO(
                0,
                "Mock Assignment",
                "2025-05-01",
                "cst438",
                10,
                1
        );

        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/assignments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());

        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);
        assertNotEquals(0, result.id());
        assertEquals("Mock Assignment", result.title());

        assignmentRepository.deleteById(result.id());
    }

    @Test
    public void addAssignmentFailsBadDate() throws Exception {
        MockHttpServletResponse response;

        AssignmentDTO dto = new AssignmentDTO(
                0,
                "Bad Date Assignment",
                "not-a-date",
                "cst438",
                10,
                1
        );

        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/assignments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto))
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
        assertTrue(response.getErrorMessage().contains("Invalid date format"));
    }

    @Test
    public void addAssignmentFailsBadSection() throws Exception {
        MockHttpServletResponse response;

        AssignmentDTO dto = new AssignmentDTO(
                0,
                "Bad Section Assignment",
                "2025-05-01",
                "cst438",
                10,
                9999
        );

        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/assignments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getErrorMessage().contains("Section not found"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/
}

