package ru.hogwarts.school_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class TestRestTemplateStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    headers = new HttpHeaders();
    headers.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
    headers.setAccept(List.of(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)));
  }

  private String getBaseUrl() {
    return "http://localhost:" + port;
  }

  private Long createTestFaculty() {
    String facultyJson = "{\"name\":\"Гриффиндор\",\"color\":\"Красный\"}";

    HttpEntity<String> request = new HttpEntity<>(facultyJson, headers);
    ResponseEntity<Faculty> response = restTemplate.postForEntity(
        getBaseUrl() + "/faculty/addFaculty",
        request,
        Faculty.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    return response.getBody().getId();
  }

  private StudentDTO createTestStudent(Long facultyId) {
    String studentJson = "{\"name\":\"Гарри Поттер\",\"age\":20,\"gender\":\"м\"}";

    HttpEntity<String> request = new HttpEntity<>(studentJson, headers);
    ResponseEntity<StudentDTO> response = restTemplate.postForEntity(
        getBaseUrl() + "/students/add?facultyId=" + facultyId,
        request,
        StudentDTO.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    return response.getBody();
  }

  @Test
  void getStudentsByName_shouldReturnStudentsByName() {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/by-name/Гарри",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гарри Поттер"));
  }

  @Test
  void getStudentsByGender_shouldReturnStudentsByGender() {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/by-gender/м",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гарри Поттер"));
  }

  @Test
  void getStudentsByAge_shouldReturnStudentsByAge() {
    Long facultyId = createTestFaculty();
    StudentDTO student = createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/by-age/" + student.getAge(),
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гарри Поттер"));
  }

  @Test
  void getStudentsByAgeRange_shouldReturnStudentsInAgeRange() {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/age-range?minAge=18&maxAge=25",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гарри Поттер"));
  }

  @Test
  void addStudent_shouldAddStudentAndReturnCreatedStatus() {
    Long facultyId = createTestFaculty();
    String studentJson = "{\"name\":\"Гермиона Грейнджер\",\"age\":20,\"gender\":\"ж\"}";

    HttpEntity<String> request = new HttpEntity<>(studentJson, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(
        getBaseUrl() + "/students/add?facultyId=" + facultyId,
        request,
        String.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гермиона Грейнджер"));
  }

  @Test
  void getStudentById_shouldReturnStudent() {
    Long facultyId = createTestFaculty();
    StudentDTO createdStudent = createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гарри Поттер"));
  }

  @Test
  void updateStudent_shouldUpdateStudent() {
    Long facultyId = createTestFaculty();
    StudentDTO createdStudent = createTestStudent(facultyId);

    String updatedStudentJson = "{\"name\":\"Рон Уизли\",\"age\":21,\"gender\":\"м\"}";

    HttpEntity<String> request = new HttpEntity<>(updatedStudentJson, headers);
    restTemplate.put(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        request);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Рон Уизли"));
  }

  @Test
  void deleteStudent_shouldDeleteStudent() {
    Long facultyId = createTestFaculty();
    StudentDTO createdStudent = createTestStudent(facultyId);

    restTemplate.delete(getBaseUrl() + "/students/delete/" + createdStudent.getId());

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}