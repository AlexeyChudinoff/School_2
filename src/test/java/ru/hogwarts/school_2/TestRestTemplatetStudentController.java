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
//@WebMvcTest
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
    Student student = new Student();
    student.setName("Test Student");
    student.setAge(20);
    student.setGender("M");

    ResponseEntity<Student> response = restTemplate.postForEntity(
        getBaseUrl() + "/add?facultyId=1",
        student,
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
  }


  @Test
  void getStudentById_ShouldReturnStudent() {
    ResponseEntity<Student> response = restTemplate.getForEntity(
        getBaseUrl() + "/1",
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getAllStudents_ShouldReturnList() {
    ResponseEntity<String> response = restTemplate.getForEntity(
        getBaseUrl() + "/all",
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void updateStudent_ShouldUpdateStudent() {
    Student student = new Student();
    student.setId(1L);
    student.setName("Updated Name");
    student.setAge(21);
    student.setGender("F");

    HttpEntity<Student> request = new HttpEntity<>(student);
    ResponseEntity<Student> response = restTemplate.exchange(
        getBaseUrl() + "/1",
        HttpMethod.PUT,
        request,
        Student.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Updated Name", response.getBody().getName());
  }

  @Test
  void deleteStudent_ShouldRemoveStudent() {
    restTemplate.delete(getBaseUrl() + "/delete/1");

    ResponseEntity<Student> response = restTemplate.getForEntity(
        getBaseUrl() + "/1",
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