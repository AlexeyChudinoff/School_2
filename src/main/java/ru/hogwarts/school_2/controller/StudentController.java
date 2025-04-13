package ru.hogwarts.school_2.controller;

import static ru.hogwarts.school_2.dto.StudentDTO.StudentDtoFromStudent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.servise.FacultyService;
import ru.hogwarts.school_2.servise.StudentService;

@RestController
@Tag(name = "Student API", description = "Управление студентами")
@RequestMapping("/student")
public class StudentController {

  StudentService studentService;
  FacultyService facultyService;

  @Autowired
  public StudentController(StudentService studentService, FacultyService facultyService) {
    this.studentService = studentService;
    this.facultyService = facultyService;
  }

  @Operation(summary = "Добавление/создание студента")
  @PostMapping("/addStudent")
  public ResponseEntity<Student> addStudent
      (@RequestBody StudentDTO studentDTO) {
    try {
      Student student = studentService.addStudent(studentDTO);
      return ResponseEntity.ok(student);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @Operation(summary = "Обновление данных студента")
  @PutMapping("/updateStudent{id}")
  public ResponseEntity<Student> updateStudent
      (@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
    if (studentService.getStudentById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
  }

  @Operation(summary = "Получение студента по ID")
  @GetMapping("/{id}")
  public ResponseEntity<StudentDTO> getStudentById
      (@PathVariable Long id) {
    Optional<Student> student = studentService.getStudentById(id);
    if (student.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Student student1 = student.get();
    StudentDTO studentDTO = StudentDtoFromStudent(student1);
    return ResponseEntity.ok(studentDTO);
  }


  @Operation(summary = "Получить студентов по возрасту")
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
          .map(student -> StudentDTO.StudentDtoFromStudent(student))
          .collect(Collectors.toList());
      return ResponseEntity.ok(studentDTOS);
    }
  }

  @Operation(summary = "Получить всех студентов")
  @GetMapping("/getAllStudents")
  public ResponseEntity<List<StudentDTO>> getAllStudents() {
    if (studentService.getAllStudents().isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(studentService.getAllStudents());
  }

  @Operation(summary = "Получить студентов по ID факультета")
  @GetMapping("/getStudentsByFacultyID")
  public List<Student> getStudentsByFacultyID(@RequestParam Long facultyID) {
    if (studentService.getStudentsByFaculty(facultyID) != null) {
      return studentService.getStudentsByFaculty(facultyID);
    } else {
      return null;
    }
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
          .map(student -> StudentDTO.StudentDtoFromStudent(student))
          .collect(Collectors.toList());
      return ResponseEntity.ok(studentDTOS);
    }
  }

  @Operation(summary = "Удаление студента по id")
  @DeleteMapping("/{id}")
  public void deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
  }

}//class
