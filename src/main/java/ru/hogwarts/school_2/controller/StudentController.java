package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
@Tag(name = "Student API", description = "Управление студентами")
public class StudentController {

  private final StudentService studentService;

  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @PostMapping
  @Operation(summary = "Добавить нового студента")
  public ResponseEntity<StudentDTO> addStudent(@RequestBody StudentDTO studentDTO) {
    return ResponseEntity.ok(studentService.addStudent(studentDTO));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить данные студента")
  public ResponseEntity<StudentDTO> updateStudent(
      @PathVariable Long id,
      @RequestBody StudentDTO studentDTO) {
    return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить студента по ID")
  public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
    return studentService.getStudentById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/age/{age}")
  @Operation(summary = "Получить студентов по возрасту")
  public ResponseEntity<List<StudentDTO>> getStudentsByAge(@PathVariable int age) {
    List<StudentDTO> students = studentService.getStudentByAge(age);
    return students.isEmpty() ?
        ResponseEntity.noContent().build() :
        ResponseEntity.ok(students);
  }

  @GetMapping
  @Operation(summary = "Получить всех студентов")
  public ResponseEntity<List<StudentDTO>> getAllStudents() {
    List<StudentDTO> students = studentService.getAllStudents();
    return students.isEmpty() ?
        ResponseEntity.noContent().build() :
        ResponseEntity.ok(students);
  }

  @GetMapping("/age-range")
  @Operation(summary = "Получить студентов по возрастному диапазону")
  public ResponseEntity<List<StudentDTO>> getStudentsByAgeRange(
      @RequestParam int minAge,
      @RequestParam int maxAge) {
    List<StudentDTO> students = studentService.getStudentsByAgeRange(minAge, maxAge);
    return students.isEmpty() ?
        ResponseEntity.noContent().build() :
        ResponseEntity.ok(students);
  }

  @GetMapping("/faculty/{facultyId}")
  @Operation(summary = "Получить студентов факультета")
  public ResponseEntity<List<StudentDTO>> getStudentsByFaculty(
      @PathVariable Long facultyId) {
    List<StudentDTO> students = studentService.getStudentsByFaculty(facultyId);
    return students.isEmpty() ?
        ResponseEntity.noContent().build() :
        ResponseEntity.ok(students);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить студента")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
    return ResponseEntity.noContent().build();
  }
}


//package ru.hogwarts.school_2.controller;
//
//import static ru.hogwarts.school_2.dto.StudentDTO.StudentDtoFromStudent;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import ru.hogwarts.school_2.dto.StudentDTO;
//import ru.hogwarts.school_2.model.Student;
//import ru.hogwarts.school_2.servise.FacultyService;
//import ru.hogwarts.school_2.servise.StudentService;
//
//@RestController
//@Tag(name = "Student API", description = "Управление студентами")
//@RequestMapping("/student")
//@Transactional(readOnly = true)
//public class StudentController {
//
//  StudentService studentService;
//  FacultyService facultyService;
//
//  @Autowired
//  public StudentController(StudentService studentService, FacultyService facultyService) {
//    this.studentService = studentService;
//    this.facultyService = facultyService;
//  }
//
//  @Operation(summary = "Добавление/создание студента")
//  @PostMapping("/addStudent")
//  public ResponseEntity<Student> addStudent
//      (@RequestBody StudentDTO studentDTO) {
//    try {
//      Student student = studentService.addStudent(studentDTO);
//      return ResponseEntity.ok(student);
//    } catch (IllegalStateException e) {
//      return ResponseEntity.badRequest().build();
//    }
//  }
//
//  @Operation(summary = "Обновление данных студента")
//  @PutMapping("/updateStudent{id}")
//  public ResponseEntity<Student> updateStudent
//      (@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
//    if (studentService.getStudentById(id).isEmpty()) {
//      return ResponseEntity.notFound().build();
//    }
//    return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
//  }
//
//  @Operation(summary = "Получение студента по ID")
//  @GetMapping("/{id}")
//  public ResponseEntity<StudentDTO> getStudentById
//      (@PathVariable Long id) {
//    Optional<Student> student = studentService.getStudentById(id);
//    if (student.isEmpty()) {
//      return ResponseEntity.notFound().build();
//    }
//    Student student1 = student.get();
//    StudentDTO studentDTO = StudentDtoFromStudent(student1);
//    return ResponseEntity.ok(studentDTO);
//  }
//
//
//  @Operation(summary = "Получить студентов по возрасту")
//  @GetMapping("/by-age/{age}")
//  public ResponseEntity<List<StudentDTO>> getStudentsByAge
//      (@PathVariable int age) {
//    if (age < 0) {
//      return ResponseEntity.badRequest().build();
//    }
//    if (age > 100) {
//      return ResponseEntity.badRequest().build();
//    }
//    List<Student> students = studentService.getStudentByAge(age);
//    if (students.isEmpty()) {
//      return ResponseEntity.noContent().build();
//    } else {
//      List<StudentDTO> studentDTOS = students.stream()
//          .map(student -> StudentDTO.StudentDtoFromStudent(student))
//          .collect(Collectors.toList());
//      return ResponseEntity.ok(studentDTOS);
//    }
//  }
//
//  @Operation(summary = "Получить всех студентов")
//  @GetMapping("/getAllStudents")
//  public ResponseEntity<List<StudentDTO>> getAllStudents() {
//    if (studentService.getAllStudents().isEmpty()) {
//      return ResponseEntity.noContent().build();
//    }
//    return ResponseEntity.ok(studentService.getAllStudents());
//  }
//
//  @Operation(summary = "Получить студентов в возрастном диапазоне от и до")
//  @GetMapping("/age-range")
//  public ResponseEntity<List<StudentDTO>> getStudentsByAgeRange(
//      @RequestParam int minAge,
//      @RequestParam int maxAge) {
//    if (minAge < 0 || maxAge < 0) {
//      throw new IllegalArgumentException("Возраст не может быть отрицательным");
//    }
//    if (minAge > maxAge) {
//      throw new IllegalArgumentException("Минимальный возраст не может превышать максимальный");
//    }
//    if (studentService.getStudentsByAgeRange(minAge, maxAge).isEmpty()) {
//      return ResponseEntity.noContent().build();
//    } else {
//      List<Student> students = studentService.getStudentsByAgeRange(minAge, maxAge);
//      List<StudentDTO> studentDTOS = students.stream()
//          .map(student -> StudentDTO.StudentDtoFromStudent(student))
//          .collect(Collectors.toList());
//      return ResponseEntity.ok(studentDTOS);
//    }
//  }
//
//  @Operation(summary = "Получить студентов по ID факультета")
//  @GetMapping("/getStudentsByFacultyID")
//  ResponseEntity<List<StudentDTO>> getStudentsByFacultyID(
//      @RequestParam Long facultyID) {
//    if (facultyService.getFacultyById(facultyID).isEmpty()) {
//      return ResponseEntity.notFound().build();
//    } else {
//      List<Student> students = studentService.getStudentsByFaculty(facultyID);
//      List<StudentDTO> studentDTOS = students.stream()
//          .map(student -> StudentDTO.StudentDtoFromStudent(student))
//          .collect(Collectors.toList());
//      return ResponseEntity.ok(studentDTOS);
//    }
//  }
//
//
//  @Operation(summary = "Удаление студента по id")
//  @DeleteMapping("/{id}")
//  public void deleteStudent(@PathVariable Long id) {
//    studentService.deleteStudent(id);
//  }
//
//}//class
