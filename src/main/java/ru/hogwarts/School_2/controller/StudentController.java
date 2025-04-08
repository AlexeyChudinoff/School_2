package ru.hogwarts.School_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.School_2.model.Student;
import ru.hogwarts.School_2.servise.FacultyService;
import ru.hogwarts.School_2.servise.StudentService;

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


}//class
