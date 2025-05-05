package ru.hogwarts.school_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.hogwarts.school_2.controller.StudentController;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebMvcTestStudentController {

  @Mock
  private StudentService studentService;

  @InjectMocks
  private StudentController studentController;

  @BeforeEach
  void setup() {
    // Имитируем HTTP-запрос для тестов
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  public void addStudent_shouldReturnCreatedResponse() {
    // Подготовка данных
    StudentDTO inputDTO = new StudentDTO(null, "Гарри Поттер", 11, "М", null);
    Long facultyId = 1L;
    Faculty faculty = new Faculty();
    faculty.setId(facultyId);

    Student savedStudent = new Student("Гарри Поттер", 11, "М");
    savedStudent.setId(1L);
    savedStudent.setFaculty(faculty);

    when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(faculty));
    when(studentService.addStudent(inputDTO, facultyId)).thenReturn(savedStudent);

    // Вызов метода
    ResponseEntity<StudentDTO> response = studentController.addStudent(inputDTO, facultyId);

    // Проверки
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertEquals("Гарри Поттер", response.getBody().getName());
    assertEquals("М", response.getBody().getGender());
  }


    @Test
    void addStudent_shouldReturnNotFoundWhenFacultyNotExists() {
      StudentDTO inputDTO = new StudentDTO(null, "Гарри Поттер", 11, "М", null);
      Long facultyId = 999L;

      when(studentService.getFacultyById(facultyId)).thenReturn(Optional.empty());

      ResponseEntity<StudentDTO> response = studentController.addStudent(inputDTO, facultyId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Тест для обновления студента
    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
      Long id = 1L;
      StudentDTO inputDTO = new StudentDTO(id, "Гарри Поттер", 12, "М", 1L);
      Student updatedStudent = new Student("Гарри Поттер", 12, "М");
      updatedStudent.setId(id);

      when(studentService.getStudentById(id)).thenReturn(Optional.of(updatedStudent));
      when(studentService.updateStudent(id, inputDTO)).thenReturn(updatedStudent);

      ResponseEntity<StudentDTO> response = studentController.updateStudent(id, inputDTO);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(StudentDTO.fromStudent(updatedStudent), response.getBody());
      assertEquals("М", response.getBody().getGender());
    }

    @Test
    void updateStudent_shouldReturnNotFoundWhenStudentNotExists() {
      Long id = 999L;
      StudentDTO inputDTO = new StudentDTO(id, "Несуществующий", 12, "М", 1L);

      when(studentService.getStudentById(id)).thenReturn(Optional.empty());

      ResponseEntity<StudentDTO> response = studentController.updateStudent(id, inputDTO);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Тест для получения всех студентов
    @Test
    void getAllStudents_shouldReturnListOfStudents() {
      Student student1 = new Student("Гарри Поттер", 11, "М");
      student1.setId(1L);
      Student student2 = new Student("Гермиона Грейнджер", 11, "Ж");
      student2.setId(2L);

      when(studentService.getAllStudents()).thenReturn(List.of(
          StudentDTO.fromStudent(student1),
          StudentDTO.fromStudent(student2)
      ));

      ResponseEntity<List<StudentDTO>> response = studentController.getAllStudents();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(2, response.getBody().size());
      assertEquals("М", response.getBody().get(0).getGender());
      assertEquals("Ж", response.getBody().get(1).getGender());
    }

    @Test
    void getAllStudents_shouldReturnNoContentWhenEmpty() {
      when(studentService.getAllStudents()).thenReturn(List.of());

      ResponseEntity<List<StudentDTO>> response = studentController.getAllStudents();

      assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // Тест для получения студента по ID
    @Test
    void getStudentById_shouldReturnStudent() {
      Long id = 1L;
      Student student = new Student("Гарри Поттер", 11, "М");
      student.setId(id);

      when(studentService.getStudentById(id)).thenReturn(Optional.of(student));

      ResponseEntity<StudentDTO> response = studentController.getStudentById(id);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(StudentDTO.fromStudent(student), response.getBody());
      assertEquals("М", response.getBody().getGender());
    }

    @Test
    void getStudentById_shouldReturnNotFound() {
      Long id = 999L;
      when(studentService.getStudentById(id)).thenReturn(Optional.empty());

      ResponseEntity<StudentDTO> response = studentController.getStudentById(id);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Тест для получения студентов по имени
    @Test
    void getStudentsByName_shouldReturnFilteredList() {
      String name = "Гарри";
      Student student = new Student("Гарри Поттер", 11, "М");
      student.setId(1L);

      when(studentService.findByNameContainingIgnoreCase(name)).thenReturn(List.of(student));

      ResponseEntity<List<StudentDTO>> response = studentController.getStudentsByName(name);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, response.getBody().size());
      assertEquals("Гарри Поттер", response.getBody().get(0).getName());
      assertEquals("М", response.getBody().get(0).getGender());
    }

    @Test
    void getStudentsByName_shouldReturnNoContent() {
      String name = "Несуществующий";
      when(studentService.findByNameContainingIgnoreCase(name)).thenReturn(List.of());

      ResponseEntity<List<StudentDTO>> response = studentController.getStudentsByName(name);

      assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // Тест для получения студентов по возрастному диапазону
    @Test
    void getStudentsByAgeRange_shouldReturnFilteredList() {
      int minAge = 10;
      int maxAge = 12;
      Student student = new Student("Гарри Поттер", 11, "М");
      student.setId(1L);

      when(studentService.getStudentsByAgeRange(minAge, maxAge)).thenReturn(List.of(student));

      ResponseEntity<List<StudentDTO>> response = studentController.getStudentsByAgeRange(minAge, maxAge);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, response.getBody().size());
      assertEquals(11, response.getBody().get(0).getAge());
      assertEquals("М", response.getBody().get(0).getGender());
    }

    // Тест для получения студентов по возрасту
    @Test
    void getStudentsByAge_shouldReturnFilteredList() {
      int age = 11;
      Student student = new Student("Гарри Поттер", age, "М");
      student.setId(1L);

      when(studentService.getStudentByAge(age)).thenReturn(List.of(student));

      ResponseEntity<List<StudentDTO>> response = studentController.getStudentsByAge(age);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, response.getBody().size());
      assertEquals(age, response.getBody().get(0).getAge());
      assertEquals("М", response.getBody().get(0).getGender());
    }

    // Тест для получения среднего возраста
    @Test
    void getAverageAge_shouldReturnValue() {
      double averageAge = 11.5;
      when(studentService.findAverageAge()).thenReturn(averageAge);

      ResponseEntity<Double> response = studentController.getAverageAge();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(averageAge, response.getBody());
    }

    // Тест для получения количества студентов факультета
    @Test
    void getCountStudents_shouldReturnCount() {
      Long facultyId = 1L;
      int count = 5;

      when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(new Faculty()));
      when(studentService.getCountStudents(facultyId)).thenReturn(count);

      ResponseEntity<Integer> response = studentController.getCountStudents(facultyId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(count, response.getBody());
    }

    // Тест для получения студентов факультета
    @Test
    void getStudentsByFacultyId_shouldReturnList() {
      Long facultyId = 1L;
      Student student = new Student("Гарри Поттер", 11, "М");
      student.setId(1L);

      when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(new Faculty()));
      when(studentService.findAllByFaculty_Id(facultyId)).thenReturn(List.of(student));

      ResponseEntity<List<StudentDTO>> response = studentController.getStudentsByFacultyId(facultyId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, response.getBody().size());
      assertEquals("М", response.getBody().get(0).getGender());
    }

    // Тест для удаления студента
    @Test
    void deleteStudentById_shouldReturnDeletedStudent() {
      Long id = 1L;
      Student student = new Student("Гарри Поттер", 11, "М");
      student.setId(id);

      when(studentService.getStudentById(id)).thenReturn(Optional.of(student));
      when(studentService.deleteStudentById(id)).thenReturn(Optional.of(student));

      ResponseEntity<Optional<Student>> response = studentController.deleteStudentById(id);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertTrue(response.getBody().isPresent());
      assertEquals("Гарри Поттер", response.getBody().get().getName());
      assertEquals("М", response.getBody().get().getGender());
    }

    // Тест для удаления всех студентов факультета
    @Test
    void deleteStudentsByFacultyId_shouldReturnDeletedList() {
      Long facultyId = 1L;
      Student student = new Student("Гарри Поттер", 11, "М");
      student.setId(1L);

      when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(new Faculty()));
      when(studentService.deleteAllStudentsFromFaculty(facultyId)).thenReturn(List.of(student));

      ResponseEntity<List<Student>> response = studentController.deleteStudentsByFacultyId(facultyId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, response.getBody().size());
      assertEquals("М", response.getBody().get(0).getGender());
    }
  }//