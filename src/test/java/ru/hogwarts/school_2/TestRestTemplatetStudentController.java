package ru.hogwarts.school_2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school_2.model.Avatar;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.AvatarRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestRestTemplatetStudentController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate; // теперь используется TestRestTemplate

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private AvatarRepository avatarRepository;

  @BeforeEach
  public void clearDb() {
    avatarRepository.deleteAll();
    studentRepository.deleteAll();
  }
  @AfterEach
  public void tearDown() {
    studentRepository.deleteAll();
  }

  private void prepareData() {
    Student student = new Student("Антон", 20, "м");
    studentRepository.save(student);
  }

  /**
   * Простой тест на создание студента.
   */
  @Test
  public void createStudentTest() {
    URI uri = URI.create("http://localhost:" + port + "/students/add?facultyId=1"); // тут передается facultyId
    Student student = new Student("Игорь Конев", 18, "м");

    ResponseEntity<String> response = restTemplate.postForEntity(uri, student, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  /**
   * Простой тест на получение всех студентов.
   */
  @Test
  public void getAllStudentsTest() {
    // Подготавливаем данные перед тестом
    prepareData();// можно так а можно сохдавать в каждом методе нового

    URI uri = URI.create("http://localhost:" + port + "/students/all");
    ResponseEntity<Object[]> response = restTemplate.getForEntity(uri, Object[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().length).isGreaterThan(0);
  }

  /**
   * Простой тест на удаление студента.
   */
  @Test
  public void deleteStudentTest() {
    // Создаем студента
    Student student = new Student("Дмитрий Медведев", 20, "м");
    studentRepository.save(student);

    URI uri = URI.create("http://localhost:" + port + "/students/delete/" + student.getId());
    restTemplate.delete(uri); // правильно используется метод delete

    // Проверяем, что студента больше нет
    assertThat(studentRepository.findById(student.getId())).isEmpty();
  }

  /**
   * Простой тест на получение студента по ID.
   */
  @Test
  public void getStudentByIdTest() {
    // Создаем студента
    Student student = new Student("Алексей Кузнецов", 22, "м");
    studentRepository.save(student);

    URI uri = URI.create("http://localhost:" + port + "/students/" + student.getId());
    ResponseEntity<Student> response = restTemplate.getForEntity(uri, Student.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Алексей Кузнецов");
  }

  /**
   * Простой тест на обновление студента.
   */
  @Test
  public void updateStudentTest() {
    // Создаем студента
    Student student = new Student("Сергей Смирнов", 21, "м");
    Student savedStudent = studentRepository.save(student); // Сохраняем и получаем сохраненного студента

    // Изменяем данные
    savedStudent.setName("Андрей Сергеев");
    savedStudent.setAge(25);

    URI uri = URI.create("http://localhost:" + port + "/students/" + savedStudent.getId()); // Используем ID из сохраненного объекта
    restTemplate.put(uri, savedStudent); // правильно используется put

    // Проверяем изменение
    Student updatedStudent = studentRepository.findById(savedStudent.getId()).get();
    assertThat(updatedStudent.getName()).isEqualTo("Андрей Сергеев");
    assertThat(updatedStudent.getAge()).isEqualTo(25);
  }


}

//package ru.hogwarts.school_2;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//import ru.hogwarts.school_2.dto.StudentDTO;
//import ru.hogwarts.school_2.model.Student;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class TestRestTemplatetStudentController {
//
//  @LocalServerPort
//  private int port;
//
//  @Autowired
//  private TestRestTemplate restTemplate;
//
//  private String baseUrl;
//  private HttpHeaders headers;
//
//  @BeforeEach
//  void setUp() {
//    baseUrl = "http://localhost:" + port;
//    headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//  }
//
//  @Test
//  void addStudent_ShouldReturnCreatedStudent() {
//    // Подготовка тестовых данных
//    StudentDTO studentDTO = new StudentDTO();
//    studentDTO.setName("Test Student");
//    studentDTO.setAge(20);
//    studentDTO.setGender("м"); // Используем "м" или "ж" вместо "M"
//    studentDTO.setFacultyId(1L); // Предполагаем, что факультет с id=1 существует
//
//    HttpEntity<StudentDTO> request = new HttpEntity<>(studentDTO, headers);
//
//    // Выполнение запроса - ожидаем StudentDTO вместо Student
//    ResponseEntity<StudentDTO> response = restTemplate.exchange(
//        baseUrl + "/students/add?facultyId=1",
//        HttpMethod.POST,
//        request,
//        StudentDTO.class);
//
//    // Проверки
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertNotNull(response.getBody().getId());
//    assertEquals("Test Student", response.getBody().getName());
//  }
//
//  @Test
//  void getStudentById_ShouldReturnStudent() {
//    // Сначала создаем студента с правильными данными
//    StudentDTO studentDTO = new StudentDTO();
//    studentDTO.setName("Test Get");
//    studentDTO.setAge(23);
//    studentDTO.setGender("м"); // Используем "м" или "ж"
//    studentDTO.setFacultyId(1L);
//
//    // Создаем студента через POST запрос
//    ResponseEntity<StudentDTO> createResponse = restTemplate.postForEntity(
//        baseUrl + "/students/add?facultyId=1",
//        studentDTO,
//        StudentDTO.class);
//
//    Long studentId = createResponse.getBody().getId();
//
//    // Затем получаем его через GET запрос, ожидая StudentDTO
//    ResponseEntity<StudentDTO> response = restTemplate.getForEntity(
//        baseUrl + "/students/" + studentId,
//        StudentDTO.class);
//
//    // Проверки
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(studentId, response.getBody().getId());
//    assertEquals("Test Get", response.getBody().getName());
//  }
//
//
//}//