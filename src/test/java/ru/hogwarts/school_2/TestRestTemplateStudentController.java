package ru.hogwarts.school_2;

import org.junit.jupiter.api.Assertions;
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
import ru.hogwarts.school_2.model.Avatar;
import ru.hogwarts.school_2.repository.AvatarRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestRestTemplateStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private AvatarRepository avatarRepository;
  @Autowired
  private StudentRepository studentRepository;

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON_UTF8));
    headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
  }

  private String getBaseUrl() {
    return "http://localhost:" + port;
  }

  private Long createTestFaculty() {
    String facultyJson = "{\"name\":\"Гриффиндор\",\"color\":\"красный\"}";
    HttpEntity<String> request = new HttpEntity<>(facultyJson, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(
        getBaseUrl() + "/faculty/addFaculty",
        request,
        Map.class);
    return Long.valueOf(response.getBody().get("id").toString());
  }

  private StudentDTO createTestStudent(Long facultyId) {
    String studentJson = "{\"name\":\"Гарри Поттер\",\"age\":17,\"gender\":\"м\"}";
    HttpEntity<String> request = new HttpEntity<>(studentJson, headers);
    ResponseEntity<StudentDTO> response = restTemplate.postForEntity(
        getBaseUrl() + "/students/add?facultyId=" + facultyId,
        request,
        StudentDTO.class);
    return response.getBody();
  }

  private void createTestAvatar(Long studentId) {
    Avatar avatar = new Avatar();
    avatar.setStudent(studentRepository.findById(studentId).orElseThrow());
    // ... установите другие свойства аватара
    avatarRepository.save(avatar);
  }


  @Test
  @Transactional
  void testDeleteStudentById() throws Exception {
    Long facultyId = createTestFaculty();
    StudentDTO student = createTestStudent(facultyId);

    // Проверяем, что студент существует
    ResponseEntity<String> getResponse = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + student.getId(),
        String.class);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());

    // Удаляем студента
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        getBaseUrl() + "/students/delete/" + student.getId(),
        HttpMethod.DELETE,
        null,
        Void.class);
    assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

    // Проверяем, что студент больше не существует
    ResponseEntity<String> getAfterDeleteResponse = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + student.getId(),
        String.class);
    assertEquals(HttpStatus.NOT_FOUND, getAfterDeleteResponse.getStatusCode());
  }

  @Test
  @Transactional
  void testDeleteAllStudentsOfFaculty() throws Exception {
    // 1. Создаем тестовые данные
    Long facultyId = createTestFaculty();
    StudentDTO student = createTestStudent(facultyId);

    // 2. Проверяем создание данных
    ResponseEntity<String> initialCheck = restTemplate.getForEntity(
        getBaseUrl() + "/students/" + student.getId(),
        String.class);
    assertEquals(HttpStatus.OK, initialCheck.getStatusCode());

    // 3. Выполняем удаление с логированием ответа
    ResponseEntity<String> deleteResponse = restTemplate.exchange(
        getBaseUrl() + "/students/delete/all/" + facultyId,
        HttpMethod.DELETE,
        null,
        String.class);

    // 4. Диагностика при ошибке
    if (deleteResponse.getStatusCode() != HttpStatus.OK) {
      System.err.println("Delete failed. Response body: " + deleteResponse.getBody());
      System.err.println("Status code: " + deleteResponse.getStatusCode());
    }

    assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

    // 5. Проверяем, что студентов больше нет
    ResponseEntity<String> finalCheck = restTemplate.getForEntity(
        getBaseUrl() + "/students/faculty/" + facultyId,
        String.class);
    assertEquals(HttpStatus.NO_CONTENT, finalCheck.getStatusCode());
  }



  // Остальные тесты остаются без изменений
  // ...


  @Test
  @Transactional
   void testGetAllStudents() throws Exception {
    Long facultyId = createTestFaculty();
    createTestStudent(facultyId);

    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/students/all",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Гарри Поттер"));
  }


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