package ru.hogwarts.school_2;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school_2.controller.FacultyController;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.AvatarRepository;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;
import ru.hogwarts.school_2.service.AvatarService;
import ru.hogwarts.school_2.service.FacultyService;
import ru.hogwarts.school_2.service.StudentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@WebMvcTest
class TestRestTemplatetFacultyController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentRepository studentRepository;

  @MockBean
  private FacultyRepository facultyRepository;

  @MockBean
  private AvatarRepository avatarRepository;

  @SpyBean
  private StudentService studentService;

  @SpyBean
  private FacultyService facultyService;

  @SpyBean
  private AvatarService avatarService;

  @InjectMocks
  private FacultyController facultyController;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/faculties";
  }

  @Test
  void addFaculty_ShouldReturnCreatedFaculty() {
    Faculty faculty = new Faculty();
    faculty.setName("Test Faculty");
    faculty.setColor("Red");

    ResponseEntity<Faculty> response = restTemplate.postForEntity(
        getBaseUrl() + "/add",
        faculty,
        Faculty.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
  }

  @Test
  void getFacultyById_ShouldReturnFaculty() {
    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/1",
        Faculty.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getAllFaculties_ShouldReturnList() {
    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/all",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void updateFaculty_ShouldUpdateFaculty() {
    Faculty faculty = new Faculty();
    faculty.setId(1L);
    faculty.setName("Updated Faculty");
    faculty.setColor("Blue");

    HttpEntity<Faculty> request = new HttpEntity<>(faculty);
    ResponseEntity<Faculty> response = restTemplate.exchange(
        getBaseUrl() + "/1",
        HttpMethod.PUT,
        request,
        Faculty.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Updated Faculty", response.getBody().getName());
  }

  @Test
  void deleteFaculty_ShouldRemoveFaculty() {
    restTemplate.delete(getBaseUrl() + "/delete/1");

    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/1",
        Faculty.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void findByNameOrColor_ShouldReturnFaculties() {
    Faculty faculty = new Faculty("Search Name", "Color1");
    faculty = restTemplate.postForObject(
        getBaseUrl() + "/add",
        faculty,
        Faculty.class);

    ResponseEntity<List> response = restTemplate.getForEntity(
        getBaseUrl() + "/find?name=Search Name",
        List.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(response.getBody().isEmpty());
  }

  @Test
  void getFacultyByStudentId_ShouldReturnFaculty() {
    Faculty faculty = new Faculty("Student Faculty", "Purple");
    faculty = restTemplate.postForObject(
        getBaseUrl() + "/add",
        faculty,
        Faculty.class);

    Student student = new Student("Faculty Student", 20, "M");
    student = restTemplate.postForObject(
        "http://localhost:" + port + "/students/add?facultyId=" + faculty.getId(),
        student,
        Student.class);

    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/by-student/" + student.getId(),
        Faculty.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(faculty.getId(), response.getBody().getId());
  }
}