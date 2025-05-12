package ru.hogwarts.school_2;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Avatar;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.AvatarRepository;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TestRestTemplateStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private AvatarRepository avatarRepository;

  private String baseUrl;
  private Long facultyId;
  private Long studentId;
  private Long avatarId;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/students";

    // Очистка данных в правильном порядке
    avatarRepository.deleteAll();
    studentRepository.deleteAll();
    facultyRepository.deleteAll();

    // Создание факультета
    Faculty faculty = new Faculty();
    faculty.setName("Гриффиндор");
    faculty.setColor("Красный");
    faculty = facultyRepository.save(faculty);
    facultyId = faculty.getId();

    // Создание студента
    Student student = new Student();
    student.setName("Гарри Поттер");
    student.setAge(17);
    student.setGender("м");
    student.setFaculty(faculty);
    student = studentRepository.save(student);
    studentId = student.getId();

    // Создание аватара
    Avatar avatar = new Avatar();
    avatar.setFilePath("/test/path");
    avatar.setFileSize(100L);
    avatar.setMediaType("image/jpeg");
    avatar.setStudent(student);
    avatar = avatarRepository.save(avatar);
    avatarId = avatar.getId();
  }

  @Test
  @DisplayName("Удаление студента с каскадным удалением аватара")
  void deleteStudent_shouldDeleteStudentAndAvatar() {
    // Проверяем, что аватар существует
    assertTrue(avatarRepository.findById(avatarId).isPresent());

    // Удаляем студента
    ResponseEntity<Void> response = restTemplate.exchange(
        baseUrl + "/delete/" + studentId,
        HttpMethod.DELETE,
        null,
        Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    // Проверяем, что студент удален
    assertFalse(studentRepository.findById(studentId).isPresent());

    // Проверяем, что аватар тоже удален
    assertFalse(avatarRepository.findById(avatarId).isPresent());
  }

  @Test
  @DisplayName("Создание студента - успешный сценарий")
  void createStudent_shouldReturnCreatedStudent() {
    StudentDTO newStudent = new StudentDTO();
    newStudent.setName("Гермиона Грейнджер");
    newStudent.setAge(17);
    newStudent.setGender("ж");

    ResponseEntity<StudentDTO> response = restTemplate.postForEntity(
        baseUrl + "/add?facultyId=" + facultyId,
        newStudent,
        StudentDTO.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody().getId());
    assertEquals("Гермиона Грейнджер", response.getBody().getName());
  }

  @Test
  @DisplayName("Получение студента по ID - успешный сценарий")
  void getStudentById_shouldReturnStudent() {
    ResponseEntity<StudentDTO> response = restTemplate.getForEntity(
        baseUrl + "/" + studentId,
        StudentDTO.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Гарри Поттер", response.getBody().getName());
  }

  @Test
  @DisplayName("Обновление студента - успешный сценарий")
  void updateStudent_shouldReturnUpdatedStudent() {
    StudentDTO updatedStudent = new StudentDTO();
    updatedStudent.setName("Гарри Джеймс Поттер");
    updatedStudent.setAge(18);
    updatedStudent.setGender("м");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<StudentDTO> request = new HttpEntity<>(updatedStudent, headers);

    ResponseEntity<StudentDTO> response = restTemplate.exchange(
        baseUrl + "/" + studentId,
        HttpMethod.PUT,
        request,
        StudentDTO.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Гарри Джеймс Поттер", response.getBody().getName());
  }

  @Test
  @DisplayName("Получение всех студентов - успешный сценарий")
  void getAllStudents_shouldReturnStudentsList() {
    ResponseEntity<List> response = restTemplate.getForEntity(
        baseUrl + "/all",
        List.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
  }

  @Test
  @DisplayName("Получение студентов по возрасту - успешный сценарий")
  void getStudentsByAge_shouldReturnFilteredList() {
    ResponseEntity<List> response = restTemplate.getForEntity(
        baseUrl + "/by-age/17",
        List.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
  }

  @Test
  @DisplayName("Получение среднего возраста - успешный сценарий")
  void getAverageAge_shouldReturnCorrectValue() {
    ResponseEntity<Double> response = restTemplate.getForEntity(
        baseUrl + "/average-age",
        Double.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(17.0, response.getBody());
  }
}