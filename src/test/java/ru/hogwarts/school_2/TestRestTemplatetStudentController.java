package ru.hogwarts.school_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRestTemplatetStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String baseUrl;
  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port;
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  @Test
  void addStudent_ShouldReturnCreatedStudent() {
    // Подготовка тестовых данных
    StudentDTO studentDTO = new StudentDTO();
    studentDTO.setName("Test Student");
    studentDTO.setAge(20);
    studentDTO.setGender("M");
    studentDTO.setFacultyId(1L); // Предполагаем, что факультет с id=1 существует

    HttpEntity<StudentDTO> request = new HttpEntity<>(studentDTO, headers);

    // Выполнение запроса
    ResponseEntity<Student> response = restTemplate.exchange(
        baseUrl + "/students/add",
        HttpMethod.POST,
        request,
        Student.class);

    // Проверки
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    assertEquals("Test Student", response.getBody().getName());
  }

  @Test
  void getStudentById_ShouldReturnStudent() {
    // Сначала создаем студента
    StudentDTO studentDTO = new StudentDTO();
    studentDTO.setName("Test Get");
    studentDTO.setAge(23);
    studentDTO.setGender("M");
    studentDTO.setFacultyId(1L);

    ResponseEntity<Student> createResponse = restTemplate.postForEntity(
        baseUrl + "/students/add",
        studentDTO,
        Student.class);

    Long studentId = createResponse.getBody().getId();

    // Затем получаем его
    ResponseEntity<Student> response = restTemplate.getForEntity(
        baseUrl + "/students/" + studentId,
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().getId());
  }
}