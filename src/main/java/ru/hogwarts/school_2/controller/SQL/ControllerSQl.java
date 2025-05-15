package ru.hogwarts.school_2.controller.SQL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.service.AvatarService;
import ru.hogwarts.school_2.service.StudentService;


@RestController
@RequestMapping("/SQL")
@Tag(name = "SQL API", description = "SQL запросы")
@Transactional
public class ControllerSQl {

  private final StudentService studentService;
  private final AvatarService avatarService;

  public ControllerSQl(AvatarService avatarService, StudentService studentService) {
    this.avatarService = avatarService;
    this.studentService = studentService;
  }

  @Operation(summary = "получить количество всех студентов")
  @GetMapping("/countStudents")
  public ResponseEntity<Long> countStudents() {
    if (studentService.getCountByAllStudens() == 0) {
      return ResponseEntity.noContent().build();
    } else {
      Long countStudents = studentService.getCountByAllStudens();
      return ResponseEntity.ok(countStudents);
    }
  }

  @Operation(summary = "Получить последних 5 студентов")
  @GetMapping("/findTop5ByOrderByIdDesc")
  public ResponseEntity<List<Student>> findTop5ByOrderByIdDesc() {
    return ResponseEntity.ok(studentService.findTop5ByOrderByIdDesc());
  }





}
