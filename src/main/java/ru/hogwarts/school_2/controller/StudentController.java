package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.servise.FacultyService;
import ru.hogwarts.school_2.servise.StudentService;
import java.util.List;
import java.util.Optional;

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
  public ResponseEntity<Student> addStudent(@RequestBody StudentDTO studentDTO) {
    try {
      Student student = studentService.addStudent(studentDTO);
      return ResponseEntity.ok(student);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @Operation(summary = "Обновление данных студента")
  @PutMapping("/updateStudent{id}")
  public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
    if (studentService.getStudentById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
  }

  @Operation(summary = "Получение студента по ID")
  @GetMapping("/{id}")
  public ResponseEntity <Optional<Student>> getStudent(@PathVariable Long id) {
    if (studentService.getStudentById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(studentService.getStudentById(id));
  }

  @Operation(summary = "Получить студентов по возрасту")
  @GetMapping("/getStudentByAge")
  public List<Student> getStudentByAge(@RequestParam int age) {
    return studentService.getStudentByAge(age);
  }

  @Operation(summary = "Получить всех студентов")
  @GetMapping("/getAllStudents")
  public ResponseEntity <List<StudentDTO>> getAllStudents() {
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
  public List<Student> getStudentsByAgeRange(
      @RequestParam int minAge,
      @RequestParam int maxAge) {
    return studentService.getStudentsByAgeRange(minAge, maxAge);
  }

  @Operation(summary = "Удаление студента по id")
  @DeleteMapping("/{id}")
  public void deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
  }

}//class
