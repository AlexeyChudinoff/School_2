package ru.hogwarts.school_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestRestTemplateStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private StudentRepository studentRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);
    headers.setAccept(List.of(APPLICATION_JSON));
    headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

    // Подготавливаем данные перед каждым тестом
    Faculty faculty = new Faculty("Гриффиндор", "Красный");
    facultyRepository.save(faculty);

    Student student = new Student("Гарри Поттер", 17, "м");
    student.setFaculty(faculty);
    studentRepository.save(student);
  }

  @AfterEach
  void tearDown() {
    // Чистка базы после каждого теста
    studentRepository.deleteAll();
    facultyRepository.deleteAll();
  }

  private String getBaseUrl() {
    return "http://localhost:" + port;
  }

  // Тест получения всех студентов
  @Test
  void testGetAllStudents() {
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<List<StudentDTO>> response =
        restTemplate.exchange(getBaseUrl() + "/students/all",
            HttpMethod.GET, entity, new ParameterizedTypeReference<List<StudentDTO>>() {});
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  // Тест получения отдельного студента по ID
  @Test
  void testGetStudentById() {
    // Получаем ID первого студента
    Long firstStudentId = studentRepository.findAll().get(0).getId();
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<StudentDTO> response =
        restTemplate.exchange(getBaseUrl() + "/students/" + firstStudentId, HttpMethod.GET,
            entity, StudentDTO.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  // Тест добавления нового студента
  @Test
  void testAddNewStudent() {
    Long facultyId = facultyRepository.findAll().get(0).getId(); // Получаем ID первого факультета
    StudentDTO studentDTO = new StudentDTO();
    studentDTO.setName("Рон Уизли");
    studentDTO.setAge(17);
    studentDTO.setGender("м");

    try {
      String jsonPayload = objectMapper.writeValueAsString(studentDTO);
      HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
      ResponseEntity<StudentDTO> response =
          restTemplate.exchange(getBaseUrl() + "/students/add?facultyId=" + facultyId, HttpMethod.POST, entity, StudentDTO.class);
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Тест обновления данных студента
  @Test
  void testUpdateStudent() {
    Long firstStudentId = studentRepository.findAll().get(0).getId(); // Получаем ID первого студента
    StudentDTO studentDTO = new StudentDTO();
    studentDTO.setName("Хермия Гранджер");
    studentDTO.setAge(18);
    studentDTO.setGender("ж");

    try {
      String jsonPayload = objectMapper.writeValueAsString(studentDTO);
      HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
      ResponseEntity<StudentDTO> response =
          restTemplate.exchange(getBaseUrl() + "/students/" + firstStudentId, HttpMethod.PUT, entity, StudentDTO.class);
      assertEquals(HttpStatus.OK, response.getStatusCode());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Тест удаления студента по ID
  @Test
  void testDeleteStudentById() {
    Long firstStudentId = studentRepository.findAll().get(0).getId(); // Получаем ID первого студента
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<Void> response =
        restTemplate.exchange(getBaseUrl() + "/students/delete/" + firstStudentId, HttpMethod.DELETE, entity, Void.class);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  // Тест удаления всех студентов факультета
  @Test
  void testDeleteAllStudentsFromFaculty() {
    Long firstFacultyId = facultyRepository.findAll().get(0).getId(); // Получаем ID первого факультета
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<Void> response =
        restTemplate.exchange(getBaseUrl() + "/students/delete/all/" + firstFacultyId, HttpMethod.DELETE, entity, Void.class);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

}