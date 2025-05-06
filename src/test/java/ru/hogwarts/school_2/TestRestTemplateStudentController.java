package ru.hogwarts.school_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import ru.hogwarts.school_2.dto.StudentDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestRestTemplateStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
  }

  private String getBaseUrl() {
    return "http://localhost:" + port;
  }

  private Long createTestFaculty()

  private Long createTestStudent(Long facultyId)

  @Test
  void testGetAllStudents() {}


  @Test
  void testDeleteStudentById() {}


  @Test
  void testDeleteAllStudentsOfFaculty() {}



  @Test
  @Transactional
  void getStudentsByAge_shouldReturnStudentsByAge() throws Exception {
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
  @Transactional
  void testAddStudent() throws Exception {
    Long facultyId = createTestFaculty();
    String studentJson = "{\"name\":\"Гермиона Грейнджер\",\"age\":21,\"gender\":\"ж\"}";

    HttpEntity<String> request = new HttpEntity<>(studentJson, headers);
    ResponseEntity<StudentDTO> response = restTemplate.postForEntity(
        getBaseUrl() + "/students/add?facultyId=" + facultyId,
        request,
        StudentDTO.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Проверка статуса ответа");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertEquals("Гермиона Грейнджер", response.getBody().getName(), "Проверка имени студента");
  }

  @Test
  @Transactional
  void testUpdateStudent() throws Exception {
    Long facultyId = createTestFaculty();
    StudentDTO createdStudent = createTestStudent(facultyId);

    String updatedStudentJson = "{\"id\":" + createdStudent.getId() +
        ",\"name\":\"Рон Уизли\",\"age\":22,\"gender\":\"м\"}";

    HttpEntity<String> request = new HttpEntity<>(updatedStudentJson, headers);
    ResponseEntity<StudentDTO> response = restTemplate.exchange(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        HttpMethod.PUT,
        request,
        StudentDTO.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка статуса обновления");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertEquals("Рон Уизли", response.getBody().getName(), "Проверка изменения имени студента");
  }



  @Test
  @Transactional
  void testGetStudentById() throws Exception {
    Long facultyId = createTestFaculty();
    StudentDTO createdStudent = createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка статуса ответа");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertTrue(response.getBody().contains("Гарри Поттер"), "Проверка возвращения правильного студента");
  }

  @Test
  @Transactional
  void testGetStudentsByName() throws Exception {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/by-name/Гарри",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка статуса ответа");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertTrue(response.getBody().contains("Гарри Поттер"), "Проверка возврата по имени");
  }

  @Test
  @Transactional
  void testGetStudentsByGender() throws Exception {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/by-gender/м",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка статуса ответа");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertTrue(response.getBody().contains("Гарри Поттер"), "Проверка возврата по полу");
  }

  @Test
  @Transactional
  void testGetStudentsByFacultyId() throws Exception {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/faculty/" + facultyId,
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка статуса ответа");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertTrue(response.getBody().contains("Гарри Поттер"), "Проверка возврата студентов по факультету");
  }

}//