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
import ru.hogwarts.school_2.controller.StudentController;
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
@WebMvcTest
class TestRestTemplatetStudentController {

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
  private StudentController studentController;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/students";
  }

  @Test
  void addStudent_ShouldReturnCreatedStudent() {
    Faculty faculty = new Faculty("Test Faculty", "Red");
    faculty = restTemplate.postForObject(
        "http://localhost:" + port + "/faculties/add",
        faculty,
        Faculty.class);

    Student student = new Student("Test Student", 20, "M");
    ResponseEntity<Student> response = restTemplate.postForEntity(
        getBaseUrl() + "/add?facultyId=" + faculty.getId(),
        student,
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
  }

  @Test
  void getStudentById_ShouldReturnStudent() {
    Faculty faculty = new Faculty("Get Faculty", "Green");
    faculty = restTemplate.postForObject(
        "http://localhost:" + port + "/faculties/add",
        faculty,
        Faculty.class);

    Student student = new Student("Test Get", 23, "M");
    student = restTemplate.postForObject(
        getBaseUrl() + "/add?facultyId=" + faculty.getId(),
        student,
        Student.class);

    ResponseEntity<Student> response = restTemplate.getForEntity(
        getBaseUrl() + "/" + student.getId(),
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(student.getId(), response.getBody().getId());
  }

  @Test
  void getAllStudents_ShouldReturnList() {
    ResponseEntity<List> response = restTemplate.getForEntity(
        getBaseUrl() + "/all",
        List.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void updateStudent_ShouldUpdateStudent() {
    Faculty faculty = new Faculty("Update Faculty", "Blue");
    faculty = restTemplate.postForObject(
        "http://localhost:" + port + "/faculties/add",
        faculty,
        Faculty.class);

    Student student = new Student("Original", 21, "F");
    student = restTemplate.postForObject(
        getBaseUrl() + "/add?facultyId=" + faculty.getId(),
        student,
        Student.class);

    student.setName("Updated");
    HttpEntity<Student> request = new HttpEntity<>(student);
    ResponseEntity<Student> response = restTemplate.exchange(
        getBaseUrl() + "/" + student.getId(),
        HttpMethod.PUT,
        request,
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Updated", response.getBody().getName());
  }

  @Test
  void deleteStudent_ShouldRemoveStudent() {
    Faculty faculty = new Faculty("Delete Faculty", "Brown");
    faculty = restTemplate.postForObject(
        "http://localhost:" + port + "/faculties/add",
        faculty,
        Faculty.class);

    Student student = new Student("To Delete", 23, "F");
    student = restTemplate.postForObject(
        getBaseUrl() + "/add?facultyId=" + faculty.getId(),
        student,
        Student.class);

    restTemplate.delete(getBaseUrl() + "/delete/" + student.getId());

    ResponseEntity<Student> response = restTemplate.getForEntity(
        getBaseUrl() + "/" + student.getId(),
        Student.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getStudentsByAgeRange_ShouldReturnStudents() {
    Faculty faculty = new Faculty("Age Faculty", "Purple");
    faculty = restTemplate.postForObject(
        "http://localhost:" + port + "/faculties/add",
        faculty,
        Faculty.class);

    Student student1 = new Student("Age 20", 20, "F");
    student1 = restTemplate.postForObject(
        getBaseUrl() + "/add?facultyId=" + faculty.getId(),
        student1,
        Student.class);

    ResponseEntity<List> response = restTemplate.getForEntity(
        getBaseUrl() + "/age-range?minAge=19&maxAge=21",
        List.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(response.getBody().isEmpty());
  }
}