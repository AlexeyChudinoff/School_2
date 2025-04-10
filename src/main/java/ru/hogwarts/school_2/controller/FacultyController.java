package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.servise.FacultyService;
import ru.hogwarts.school_2.servise.StudentService;

@RestController
@Tag(name = "Faculty API", description = "Управление факультетами")
@RequestMapping("/faculty")
@Validated
public class FacultyController {

  private final FacultyService facultyService;
  private final StudentService studentService;
  private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);

  @Autowired
  public FacultyController(FacultyService facultyService, StudentService studentService) {
    this.facultyService = facultyService;
    this.studentService = studentService;
  }


  @Operation(summary = "Добавление/Создание факультета")
  @PostMapping("/addFaculty")
  public ResponseEntity<Faculty> createFaculty(@Valid @RequestBody Faculty faculty) {
    logger.info("Запрос на добавление факультета: {}", faculty.getName());
    Faculty createdFaculty = facultyService.addFaculty(faculty);
    logger.info("Факультет успешно создан: {}", createdFaculty.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
  }

  @Operation(summary = "Получение факультета по цвету")
  @GetMapping("/getFacultyByColor")
  public ResponseEntity<Faculty> getFacultyByColor(
      @RequestParam("color") @NotBlank String color) {
    logger.info("Запрос на получение факультета по цвету: {}", color);
    Faculty faculty = facultyService.getFacultyByColor(color);
    if (faculty == null) {
      logger.warn("Факультет с цветом {} не найден", color);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(faculty);
  }

  @Operation(summary = "Получение факультета по ID")
  @GetMapping("/getFacultyById")
  public ResponseEntity<Optional<Faculty>> getFacultyById(@RequestParam Long id) {
    logger.info("Запрос на получение факультета по ID: {}", id);
    Optional<Faculty> faculty = facultyService.getFacultyById(id);
    if (faculty.isEmpty()) {
      logger.warn("Факультет с ID {} не найден", id);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(faculty);
  }

  @Operation(summary = "Получение факультета по имени или цвету")
  @GetMapping("/searchByNameOrColor")
  public ResponseEntity<List<Faculty>> searchFaculties(
      @RequestParam(required = false) String name,// требовать не обязательно
      @RequestParam(required = false) String color) {
    logger.info("Запрос на поиск факультетов по имени: {} или цвету: {}", name, color);
    List<Faculty> faculties = facultyService.findByNameOrColor(name, color);
    if (faculties.isEmpty()) {
      logger.warn("Факультеты по заданным критериям не найдены");
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(faculties);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
    logger.error("Ошибка при обработке запроса: {}", e.getMessage());
    return ResponseEntity.badRequest().body(e.getMessage());
  }

}//class
