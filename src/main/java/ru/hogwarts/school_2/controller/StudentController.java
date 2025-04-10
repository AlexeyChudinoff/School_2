package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
  public void addStudent(@RequestBody Student student) {
    studentService.addStudent(student);
  }
  @Operation(summary = "Обновление данных студента")
  @PutMapping("/updateStudent{id}")
  public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
    return studentService.updateStudent(id, student);
  }

  @Operation(summary = "Получение студента по ID")
  @GetMapping("/{id}")
  public Optional<Student> getStudent(@PathVariable Long id) {
    return studentService.getStudentById(id);
  }

  @Operation(summary = "Получить студентов по возрасту")
  @GetMapping("/getStudentByAge")
  public List<Student> getStudentByAge(@RequestParam int age) {
    return studentService.getStudentByAge(age);
  }

  @Operation(summary = "Получить всех студентов")
  @GetMapping("/getAllStudents")
  public List<Student> getAllStudents() {
    if (studentService.getAllStudents() != null) {
      return studentService.getAllStudents();
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
