package com.cst438.service;

import com.cst438.domain.*;
import com.cst438.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    TermRepository termRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "gradebook_service")
    public void receiveFromRegistrar(String message) {
        try {
            String[] parts = message.split(" ", 2);
            String action = parts[0];
            String data = parts.length > 1 ? parts[1] : null;

            switch (action) {
                case "addCourse":
                case "updateCourse":
                    CourseDTO courseDTO = fromJsonString(data, CourseDTO.class);
                    Course course = new Course();
                    course.setCourseId(courseDTO.courseId());
                    course.setTitle(courseDTO.title());
                    course.setCredits(courseDTO.credits());
                    courseRepository.save(course);
                    break;
                case "deleteCourse":
                    courseRepository.deleteById(data);
                    break;

                case "addUser":
                case "updateUser":
                    UserDTO userDTO = fromJsonString(data, UserDTO.class);
                    User user = new User();
                    user.setId(userDTO.id());
                    user.setName(userDTO.name());
                    user.setEmail(userDTO.email());
                    user.setType(userDTO.type());
                    userRepository.save(user);
                    break;
                case "deleteUser":
                    userRepository.deleteById(Integer.parseInt(data));
                    break;

                case "addSection":
                case "updateSection":
                    SectionDTO sectionDTO = fromJsonString(data, SectionDTO.class);
                    Section section = new Section();
                    section.setSectionNo(sectionDTO.secNo());
                    section.setSecId(sectionDTO.secId());
                    section.setBuilding(sectionDTO.building());
                    section.setRoom(sectionDTO.room());
                    section.setTimes(sectionDTO.times());
                    section.setInstructor_email(sectionDTO.instructorEmail());
                    Course c = courseRepository.findById(sectionDTO.courseId()).orElse(null);
                    Term t = termRepository.findByYearAndSemester(sectionDTO.year(), sectionDTO.semester());
                    section.setCourse(c);
                    section.setTerm(t);
                    sectionRepository.save(section);
                    break;
                case "deleteSection":
                    sectionRepository.deleteById(Integer.parseInt(data));
                    break;

                case "addEnrollment":
                case "updateEnrollment":
                    EnrollmentDTO eDTO = fromJsonString(data, EnrollmentDTO.class);
                    Enrollment e = new Enrollment();
                    e.setEnrollmentId(eDTO.enrollmentId());
                    e.setGrade(eDTO.grade());
                    e.setUser(userRepository.findById(eDTO.studentId()).orElse(null));
                    e.setSection(sectionRepository.findById(eDTO.sectionNo()).orElse(null));
                    enrollmentRepository.save(e);
                    break;
                case "deleteEnrollment":
                    enrollmentRepository.deleteById((int) Long.parseLong(data));
                    break;

                default:
                    System.out.println("Unknown action: " + action);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + ". Error: " + e.getMessage());
        }
    }

    public void updateEnrollment(EnrollmentDTO dto) {
        String msg = "updateEnrollment " + asJsonString(dto);
        sendMessage(msg);
    }

    private void sendMessage(String s) {
        rabbitTemplate.convertAndSend(registrarServiceQueue.getName(), s);
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
    }
}
