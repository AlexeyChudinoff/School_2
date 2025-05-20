package ru.hogwarts.school_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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

@ExtendWith(MockitoExtension.class)
public class WebMvcTestStudentController {

  @Mock
  private StudentService studentService;

  @InjectMocks
  private StudentController studentController;

  @BeforeEach
  void setup() {
    // Для работы методов контроллера, использующих RequestContextHolder
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  public void addStudent_shouldReturnCreatedResponse() {
    StudentDTO inputDTO = new StudentDTO(null, "Гарри Поттер", 11, "М", 1L);
    Long facultyId = 1L;
    Faculty faculty = new Faculty();
    faculty.setId(facultyId);

    Student savedStudent = new Student("Гарри Поттер", 11, "М");
    savedStudent.setId(1L);
    savedStudent.setFaculty(faculty);

    when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(faculty));
    when(studentService.addStudent(inputDTO, facultyId)).thenReturn(savedStudent);

    ResponseEntity<StudentDTO> response = studentController.addStudent(inputDTO, facultyId);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertEquals("Гарри Поттер", response.getBody().getName());
    assertEquals("М", response.getBody().getGender());
  }

  @Test
  void addStudent_shouldReturnNotFoundWhenFacultyNotExists() {
    StudentDTO inputDTO = new StudentDTO(null, "Гарри Поттер", 11, "М", 999L);
    Long facultyId = 999L;

    when(studentService.getFacultyById(facultyId)).thenReturn(Optional.empty());

    ResponseEntity<StudentDTO> response = studentController.addStudent(inputDTO, facultyId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void updateStudent_shouldReturnUpdatedStudent() throws NotFoundException {
    Long id = 1L;
    StudentDTO inputDTO = new StudentDTO(id, "Гарри Поттер", 12, "М", 1L);
    Student updatedStudent = new Student("Гарри Поттер", 12, "М");
    updatedStudent.setId(id);

    when(studentService.getStudentById(id)).thenReturn(Optional.of(updatedStudent));
    when(studentService.updateStudent(id, inputDTO)).thenReturn(updatedStudent);

    ResponseEntity<StudentDTO> response = studentController.updateStudent(id, inputDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(StudentDTO.fromStudent(updatedStudent), response.getBody());
    assertEquals("М", Objects.requireNonNull(response.getBody()).getGender());
  }

  @Test
  void updateStudent_shouldReturnNotFoundWhenServiceThrowsNotFoundException()
      throws NotFoundException {
    Long id = 1L;
    StudentDTO inputDTO = new StudentDTO(id, "Гарри Поттер", 12, "М", 1L);

    when(studentService.getStudentById(id)).thenReturn(Optional.of(new Student()));
    when(studentService.updateStudent(id, inputDTO)).thenThrow(new NotFoundException());

    ResponseEntity<StudentDTO> response = studentController.updateStudent(id, inputDTO);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }


  @Test
  void getAllStudents_shouldReturnListOfStudents() {
    Student student1 = new Student("Гарри Поттер", 11, "М");
    student1.setId(1L);
    Student student2 = new Student("Гермиона Грейнджер", 11, "Ж");
    student2.setId(2L);

    when(studentService.getAllStudents()).thenReturn(List.of(
        StudentDTO.fromStudent(student1),
        StudentDTO.fromStudent(student2)));

    ResponseEntity<List<StudentDTO>> response = studentController.getAllStudents();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    assertEquals("М", response.getBody().get(0).getGender());
    assertEquals("Ж", response.getBody().get(1).getGender());
  }

  @Test
  void getAllStudents_shouldReturnEmptyList() {
    when(studentService.getAllStudents()).thenReturn(Collections.emptyList());

    ResponseEntity<List<StudentDTO>> response = studentController.getAllStudents();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

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

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

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

  @Test
  void getAverageAge_shouldReturnValue() {
    double averageAge = 11.5;
    when(studentService.findAverageAge()).thenReturn(averageAge);

    ResponseEntity<Double> response = studentController.getAverageAge();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(averageAge, response.getBody());
  }

  @Test
  void getCountStudents_shouldReturnCount() {
    Long facultyId = 1L;
    long count = 5;

    when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(new Faculty()));
    when(studentService.getCountStudents(facultyId)).thenReturn(count);

    ResponseEntity<Long> response = studentController.getCountStudents(facultyId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(count, response.getBody());
  }

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

  @Test
  void deleteStudentById_existingStudent_returnsNoContent() {
    Long id = 1L;
    when(studentService.deleteStudentById(id)).thenReturn(true);

    ResponseEntity<Void> response = studentController.deleteStudentById(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void deleteStudentById_nonExistingStudent_returnsNotFound() {
    Long id = 999L;
    when(studentService.deleteStudentById(id)).thenReturn(false);

    ResponseEntity<Void> response = studentController.deleteStudentById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
