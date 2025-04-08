package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

  @PostMapping("/addStudent")
  public void addStudent(@RequestBody Student student) {
    studentService.addStudent(student);
  }

  @GetMapping("/getStudentByAge")
  public Student getStudentByAge(@RequestParam int age) {
    return studentService.getStudentByAge(age);
  }
  @GetMapping("/getAllStudents")
  public List< Student> getAllStudents() {
    if (studentService.getAllStudents() != null) {
      return studentService.getAllStudents();
    } else {
      return null;
    }
  }


}//class
