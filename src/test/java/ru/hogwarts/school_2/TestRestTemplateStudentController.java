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
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) //Аннотация @DirtiesContext
// помечает контекст как грязный после каждого теста, заставляя Spring перезагружать
// приложение и очистить все данные между тестами.
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
  @Transactional
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
  void testGetAllStudents() throws Exception {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/students/all", String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка статуса ответа");
    assertNotNull(response.getBody(), "Проверка наличия тела ответа");
    assertTrue(response.getBody().contains("Гарри Поттер"), "Проверка присутствия первого студента");
    assertTrue(response.getBody().contains("Гермиона Грейнджер"), "Проверка присутствия второго студента");
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

  @Test
  @Transactional
  void testDeleteStudentById() throws Exception {
    Long facultyId = createTestFaculty();
    StudentDTO createdStudent = createTestStudent(facultyId);

    ResponseEntity<Void> response = restTemplate.exchange(
        getBaseUrl() + "/students/delete/" + createdStudent.getId(),
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка удаления студента");

    ResponseEntity<String> afterResponse = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + createdStudent.getId(),
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, afterResponse.getStatusCode(), "Проверка, что студент действительно удалён");
  }

  @Test
  @Transactional
  void testDeleteAllStudentsOfFaculty() throws Exception {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);
    createTestStudent(facultyId);

    ResponseEntity<Void> response = restTemplate.exchange(
        getBaseUrl() + "/students/delete/all/" + facultyId,
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Проверка удаления всех студентов факультета");

    ResponseEntity<String> afterResponse = restTemplate.getForEntity(
        getBaseUrl() + "/students/faculty/" + facultyId,
        String.class);

    assertEquals(HttpStatus.NO_CONTENT, afterResponse.getStatusCode(), "Проверка, что больше нет студентов на факультете");
  }

}//