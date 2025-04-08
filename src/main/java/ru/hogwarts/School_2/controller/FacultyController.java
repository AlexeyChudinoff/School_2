package ru.hogwarts.School_2.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.School_2.model.Faculty;
import ru.hogwarts.School_2.servise.FacultyService;
import ru.hogwarts.School_2.servise.StudentService;

@RequestMapping("/faculty")
public class FacultyController {

  FacultyService facultyService;
  StudentService studentService;
  public FacultyController(FacultyService facultyService, StudentService studentService) {
    this.facultyService = facultyService;
    this.studentService = studentService;
  }

  @PostMapping("/addFaculty")
  public Faculty creatFaculty(@RequestBody Faculty faculty) {
    return facultyService.addFaculty(faculty);
  }
  @GetMapping("/getfacultyByColor")
  public Faculty readFacultyByColor(@RequestParam("color") String color) {
    return facultyService.getFacultyByColor(color);
  }

  @GetMapping("/getFaculty {Id}")
  public Faculty getFacultyById(@RequestParam Long id) {
    return facultyService.getFacultyById(id);
  }






}//class
