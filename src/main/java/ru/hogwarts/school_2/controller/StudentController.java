package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.service.StudentService;

@RestController
@RequestMapping("/students")
@Tag(name = "Student API", description = "Управление студентами")
@Validated

public class StudentController {

  private final StudentService studentService;

  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @Transactional
  @Operation(summary = "Добавить нового студента")
  @PostMapping("/add")
  public ResponseEntity<StudentDTO> addStudent(
      @Valid @RequestBody StudentDTO studentDTO,
      @RequestParam Long facultyId) {

    if (studentService.getFacultyById(facultyId).isEmpty()) {
      return ResponseEntity.notFound().build(); // 404 NOT FOUND, если факультет не найден
    }

    StudentDTO savedStudent = StudentDTO.fromStudent(
        studentService.addStudent(studentDTO, facultyId));

    // Формируем URL только в продуктивной среде
    if (RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes &&
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
            != null) {
      URI location = ServletUriComponentsBuilder.fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(savedStudent.getId())
          .toUri();
      return ResponseEntity.created(location).body(savedStudent);
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }
  }

  @Transactional
  @Operation(summary = "Обновить данные студента")
  @PutMapping("/{id}")
  public ResponseEntity<StudentDTO> updateStudent(
      @PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
    if (studentService.getStudentById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    studentDTO.setId(id);
    try {
      Student updatedStudent = studentService.updateStudent(id, studentDTO);
      if (updatedStudent == null) {
        return ResponseEntity.notFound().build();
      }
      StudentDTO updateStudentDTO = StudentDTO.fromStudent(updatedStudent);
      return ResponseEntity.ok(updateStudentDTO);
    } catch (NotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }


  @Operation(summary = "Получить всех студентов")
  @GetMapping("/all")
  public ResponseEntity<List<StudentDTO>> getAllStudents() {
    List<StudentDTO> students = studentService.getAllStudents();
    return
        ResponseEntity.ok(students); // всегда возврат HTTP-код 200 OK
  }

  @Operation(summary = "Получить студента по ID")
  @GetMapping("/{id}")
  public ResponseEntity<StudentDTO> getStudentById
      (@PathVariable Long id) {
    if (studentService.getStudentById(id).isEmpty()) {
      return ResponseEntity.notFound().build();//"Студент с таким ID не найден."
    }
    StudentDTO findStudent = StudentDTO.fromStudent(studentService.getStudentById(id).get());
    return ResponseEntity.ok(findStudent);
  }

  @Operation(summary = "Получить студентов по имени")
  @GetMapping("/by-name/{name}")
  public ResponseEntity<List<StudentDTO>> getStudentsByName(@PathVariable String name) {
    List<Student> students = studentService.findByNameContainingIgnoreCase(name);
    List<StudentDTO> studentDTOS = students.stream()
        .map(StudentDTO::fromStudent)
        .collect(Collectors.toList());

    return ResponseEntity.ok(studentDTOS); // Всегда возвращаем 200 OK
  }

  @Operation(summary = "Получить студентов одного пола")
  @GetMapping("/by-gender/{gender}")
  public ResponseEntity<List<StudentDTO>> getStudentsByGender
      (@PathVariable String gender) {
    List<Student> students = studentService.findByGender(gender);
    if (students.isEmpty()) {
      return ResponseEntity.noContent().build();//"Студентов с таким полом не найдено."
    }
    List<StudentDTO> studentDTOS = students.stream()
        .map(StudentDTO::fromStudent)
        .collect(Collectors.toList());
    return ResponseEntity.ok(studentDTOS);// Возвращаем список студентов
  }

  @Operation(summary = "Получить студентов в возрастном диапазоне от и до")
  @GetMapping("/age-range")
  public ResponseEntity<List<StudentDTO>> getStudentsByAgeRange(
      @RequestParam int minAge,
      @RequestParam int maxAge) {
    if (minAge < 0 || maxAge < 0) {
      throw new IllegalArgumentException("Возраст не может быть отрицательным");
    }
    if (minAge > maxAge) {
      throw new IllegalArgumentException("Минимальный возраст не может превышать максимальный");
    }
    if (studentService.getStudentsByAgeRange(minAge, maxAge).isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      List<Student> students = studentService.getStudentsByAgeRange(minAge, maxAge);
      List<StudentDTO> studentDTOS = students.stream()
          .map(student -> StudentDTO.fromStudent(student))
          .collect(Collectors.toList());
      return ResponseEntity.ok(studentDTOS);
    }
  }

  @Operation(summary = "Получить список студентов одного возраста")
  @GetMapping("/by-age/{age}")
  public ResponseEntity<List<StudentDTO>> getStudentsByAge
      (@PathVariable int age) {
    if (age < 0) {
      return ResponseEntity.badRequest().build();
    }
    if (age > 100) {
      return ResponseEntity.badRequest().build();
    }
    List<Student> students = studentService.getStudentByAge(age);
    if (students.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      List<StudentDTO> studentDTOS = students.stream()
          .map(student -> StudentDTO.fromStudent(student))
          .collect(Collectors.toList());
      return ResponseEntity.ok(studentDTOS);
    }
  }

  @Operation(summary = "Получить количество студентов факультета по ID факультета")
  @GetMapping("/count/{facultyId}")
  public ResponseEntity<Long> getCountStudents
      (@PathVariable Long facultyId) {
    if (studentService.getFacultyById(facultyId).isEmpty()) {
      return ResponseEntity.notFound().build();//"Факультет с таким ID не найден."
    }
    Long countStudents = studentService.getCountStudents(facultyId);
    return ResponseEntity.ok(countStudents);

  }

  @Operation(summary = "Получить студентов факультета по ID факультета")
  @GetMapping("/faculty/{Id}")
  public ResponseEntity<List<StudentDTO>> getStudentsByFacultyId
      (@PathVariable Long Id) {
    if (studentService.getFacultyById(Id).isEmpty()) {
      return ResponseEntity.notFound().build();//"Факультет с таким ID не найден."
    }
    List<Student> students = studentService.findAllByFaculty_Id(Id);
    if (students.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      List<StudentDTO> studentDTOS = students.stream()
          .map(student -> StudentDTO.fromStudent(student))
          .collect(Collectors.toList());
      return ResponseEntity.ok(studentDTOS);
    }
  }

  @Transactional
  @Operation(summary = "Удалить студента по ID")
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteStudentById(@PathVariable Long id) {
    boolean deleted = studentService.deleteStudentById(id);

    if (deleted) {
      return ResponseEntity.noContent().build(); // 204 NO CONTENT — удаление прошло успешно
    } else {
      return ResponseEntity.notFound().build(); // 404 NOT FOUND — студента с таким ID не существует
    }
  }

//sql

  @Transactional
  @Operation(summary = "удалить всех студентов факультета")
  @DeleteMapping("/delete/all/{facultyId}")
  public ResponseEntity<Void> deleteStudentsByFacultyId(@PathVariable Long facultyId) {
    if (studentService.getFacultyById(facultyId).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    studentService.deleteAllStudentsFromFaculty(facultyId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Получить средний возраст студентов")
  @GetMapping("/average-age")
  public ResponseEntity<Double> getAverageAge() {
    Double averageAge = studentService.findAverageAge();
    return ResponseEntity.ok(averageAge);
  }

  @Operation(summary = "Получить количество всех студентов")
  @GetMapping("/all-students")
  public ResponseEntity<Long> getCountByAllStudens() {
    if (studentService.getCountByAllStudens() == 0) {
      return ResponseEntity.notFound().build();
    } else {
      Long count = studentService.getCountByAllStudens();
      return ResponseEntity.ok(count);
    }
  }

  @Operation(summary = "получаем 5 последних по айди студентов")
  @GetMapping("/last-five")
  public ResponseEntity<List<StudentDTO>> getLastFiveStudents() {
    if (studentService.findTop5ByOrderByIdDesc().isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      List<Student> students = studentService.findTop5ByOrderByIdDesc();
      List<StudentDTO> studentDTOS = students.stream()
          .map(StudentDTO::fromStudent)
          .collect(Collectors.toList());
      return ResponseEntity.ok(studentDTOS);
  }
  }

}//class