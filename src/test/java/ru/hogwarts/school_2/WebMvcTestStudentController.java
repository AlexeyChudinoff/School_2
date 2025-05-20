package ru.hogwarts.school_2;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
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
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }
  /**
   * Тестирование успешной регистрации нового студента.
   */
  @Test
  public void addStudent_shouldReturnCreatedResponse() {
    // Данные для теста
    StudentDTO inputDTO = new StudentDTO(null, "Гарри Поттер", 11, "М", null);
    Long facultyId = 1L;
    Faculty faculty = new Faculty();
    faculty.setId(facultyId);

    Student savedStudent = new Student("Гарри Поттер", 11, "М");
    savedStudent.setId(1L);
    savedStudent.setFaculty(faculty);

    // Эмулируем сервисы
    when(studentService.getFacultyById(facultyId)).thenReturn(Optional.of(faculty));
    when(studentService.addStudent(inputDTO, facultyId)).thenReturn(savedStudent);

    // Запускаем тест
    ResponseEntity<StudentDTO> response = studentController.addStudent(inputDTO, facultyId);

    // Проверки
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertEquals("Гарри Поттер", response.getBody().getName());
    assertEquals("М", response.getBody().getGender());
  }

  /**
   * Тестирование неудачной попытки зарегистрировать студента на несуществующем факультете.
   */
  @Test
  void addStudent_shouldReturnNotFoundWhenFacultyNotExists() {
    StudentDTO inputDTO = new StudentDTO(null, "Гарри Поттер", 11, "М", null);
    Long facultyId = 999L;

    when(studentService.getFacultyById(facultyId)).thenReturn(Optional.empty());

    ResponseEntity<StudentDTO> response = studentController.addStudent(inputDTO, facultyId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  /**
   * Тестирование успешной модификации данных студента.
   */
  @Test
  void updateStudent_shouldReturnUpdatedStudent() throws Exception { // Добавляем объявление исключения
    Long id = 1L;
    StudentDTO inputDTO = new StudentDTO(id, "Гарри Поттер", 12, "М", 1L);
    Student updatedStudent = new Student("Гарри Поттер", 12, "М");
    updatedStudent.setId(id);

    // Ожидаемое поведение сервисов
    when(studentService.getStudentById(id)).thenReturn(Optional.of(updatedStudent)); // Проверяем наличие студента
    when(studentService.updateStudent(id, inputDTO)).thenReturn(updatedStudent); // Обновление студента

    // Вызываем контроллер
    ResponseEntity<StudentDTO> response = studentController.updateStudent(id, inputDTO);

    // Проверка результатов
    assertEquals(HttpStatus.OK, response.getStatusCode()); // Статус OK
    assertEquals(StudentDTO.fromStudent(updatedStudent), response.getBody()); // Проверка тела ответа
    assertEquals("М", Objects.requireNonNull(response.getBody()).getGender()); // Пол студента
  }

  /**
   * Тестирование неудачного обновления несуществующего студента.
   */
  @Test
  public void updateStudent_shouldReturnNotFoundWhenStudentNotExists() {
    Long id = 999L;
    StudentDTO inputDTO = new StudentDTO(id, "Несуществующий", 12, "М", 1L);

    when(studentService.getStudentById(id)).thenReturn(Optional.empty());

    try {
      studentController.updateStudent(id, inputDTO);
      fail("Ожидалось исключение NotFoundException");
      // Если исключение не было брошено, тест провалится
    } catch (NotFoundException e) {
      // Успешно поймали исключение, ничего дополнительно проверять не надо
    }
  }

  /**
   * Тестирование успешного возвращения полного списка студентов.
   */
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

  /**
   * Тестирование возвращения пустого списка студентов.
   */
  @Test
  void getAllStudents_shouldReturnEmptyList() {
    when(studentService.getAllStudents()).thenReturn(Collections.emptyList());

    ResponseEntity<List<StudentDTO>> response = studentController.getAllStudents();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

  /**
   * Тестирование успешного получения конкретного студента по идентификатору.
   */
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

  /**
   * Тестирование невозможности получить несуществующего студента.
   */
  @Test
  void getStudentById_shouldReturnNotFound() {
    Long id = 999L;
    when(studentService.getStudentById(id)).thenReturn(Optional.empty());

    ResponseEntity<StudentDTO> response = studentController.getStudentById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  /**
   * Тестирование фильтра студентов по частичному совпадению имени.
   */
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

  /**
   * Тестирование фильтрации студентов по частичному имени с отсутствием результатов.
   */
  @Test
  void getStudentsByName_shouldReturnNoContent() {
    String name = "Несуществующий";
    when(studentService.findByNameContainingIgnoreCase(name)).thenReturn(List.of());

    ResponseEntity<List<StudentDTO>> response = studentController.getStudentsByName(name);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

  /**
   * Тестирование выбора студентов по возрастной группе.
   */
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

  /**
   * Тестирование выборки студентов по определенному возрасту.
   */
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

  /**
   * Тестирование вычисления среднего возраста студентов.
   */
  @Test
  void getAverageAge_shouldReturnValue() {
    double averageAge = 11.5;
    when(studentService.findAverageAge()).thenReturn(averageAge);

    ResponseEntity<Double> response = studentController.getAverageAge();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(averageAge, response.getBody());
  }

  /**
   * Тестирование подсчета числа студентов определенного факультета.
   */
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

  /**
   * Тестирование выборки студентов одного факультета.
   */
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
    when(studentService.deleteStudentById(id)).thenReturn(true); // Студент успешно удалён

    ResponseEntity<Void> response = studentController.deleteStudentById(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Ожидаем 204 NO CONTENT
  }

  @Test
  void deleteStudentById_nonExistingStudent_returnsNotFound() {
    Long id = 999L;
    when(studentService.deleteStudentById(id)).thenReturn(false); // Студент не найден

    ResponseEntity<Void> response = studentController.deleteStudentById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Ожидаем 404 NOT FOUND
  }

}//

