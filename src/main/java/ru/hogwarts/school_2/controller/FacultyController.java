//package ru.hogwarts.school_2.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import ru.hogwarts.school_2.dto.FacultyDTO;
//import ru.hogwarts.school_2.model.Faculty;
//import ru.hogwarts.school_2.service.FacultyService;
//
//import java.util.List;
//
//@RestController
//@Tag(name = "Faculty API", description = "Управление факультетами")
//@RequestMapping("/faculty")
//@Validated
//public class FacultyController {
//
//  private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);
//  private final FacultyService facultyService;
//
//  public FacultyController(FacultyService facultyService) {
//    this.facultyService = facultyService;
//  }
//
//  @PostMapping
//  @Operation(summary = "Добавление факультета")
//  public ResponseEntity<Faculty> createFaculty(@Valid @RequestBody Faculty faculty) {
//    logger.info("Запрос на добавление факультета: {}", faculty.getName());
//    Faculty createdFaculty = facultyService.addFaculty(faculty);
//    return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
//  }
//
//  @PutMapping("/{id}")
//  @Operation(summary = "Обновление факультета")
//  public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @Valid @RequestBody Faculty faculty) {
//    logger.info("Запрос на обновление факультета с ID: {}", id);
//    faculty.setId(id);
//    Faculty updatedFaculty = facultyService.updateFaculty(faculty);
//    return ResponseEntity.ok(updatedFaculty);
//  }
//
//  @GetMapping("/color/{color}")
//  @Operation(summary = "Получение факультета по цвету")
//  public ResponseEntity<Faculty> getFacultyByColor(@PathVariable @NotBlank String color) {
//    logger.info("Запрос на получение факультета по цвету: {}", color);
//    return facultyService.getFacultyByColor(color)
//        .map(ResponseEntity::ok)
//        .orElseGet(() -> {
//          logger.warn("Факультет с цветом {} не найден", color);
//          return ResponseEntity.notFound().build();
//        });
//  }
//
//  @GetMapping("/name/{name}")
//  @Operation(summary = "Получение факультета по имени")
//  public ResponseEntity<Faculty> getFacultyByName(@PathVariable @NotBlank String name) {
//    logger.info("Запрос на получение факультета по имени: {}", name);
//    return facultyService.getFacultyByName(name)
//        .map(ResponseEntity::ok)
//        .orElseGet(() -> {
//          logger.warn("Факультет с именем {} не найден", name);
//          return ResponseEntity.notFound().build();
//        });
//  }
//
//  @GetMapping("/{id}")
//  @Operation(summary = "Получение факультета по ID")
//  public ResponseEntity<Faculty> getFacultyById(@PathVariable @NotNull Long id) {
//    logger.info("Запрос на получение факультета по ID: {}", id);
//    return facultyService.getFacultyById(id)
//        .map(ResponseEntity::ok)
//        .orElseGet(() -> {
//          logger.warn("Факультет с ID {} не найден", id);
//          return ResponseEntity.notFound().build();
//        });
//  }
//
//  @GetMapping("/search")
//  @Operation(summary = "Поиск факультетов по имени или цвету")
//  public ResponseEntity<List<Faculty>> searchFaculties(
//      @RequestParam(required = false) String name,
//      @RequestParam(required = false) String color) {
//    logger.info("Запрос на поиск факультетов по имени: {} или цвету: {}", name, color);
//    List<Faculty> faculties = facultyService.findByNameOrColor(name, color);
//    return faculties.isEmpty() ?
//        ResponseEntity.noContent().build() :
//        ResponseEntity.ok(faculties);
//  }
//
//  @GetMapping("/student/{studentId}")
//  @Operation(summary = "Получение факультета по ID студента")
//  public ResponseEntity<FacultyDTO> getFacultyByStudentId
//      (@PathVariable @NotNull Long studentId) {
//    logger.info("Запрос на получение факультета по ID студента: {}", studentId);
//    try {
//      FacultyDTO facultyDTO = facultyService.getFacultyByStudentId(studentId);
//      return ResponseEntity.ok(facultyDTO);
//    } catch (EntityNotFoundException e) {
//      logger.warn("Студент с ID {} не найден", studentId);
//      return ResponseEntity.notFound().build();
//    } catch (IllegalStateException e) {
//      logger.warn("У студента с ID {} нет факультета", studentId);
//      return ResponseEntity.badRequest().body(new FacultyDTO(null, "N/A", "N/A"));
//    }
//  }
//
//  @GetMapping
//  @Operation(summary = "Получение всех факультетов")
//  public ResponseEntity<List<Faculty>> getAllFaculties() {
//    logger.info("Запрос на получение всех факультетов");
//    List<Faculty> faculties = facultyService.getAllFaculties();
//    return faculties.isEmpty() ?
//        ResponseEntity.noContent().build() :
//        ResponseEntity.ok(faculties);
//  }
// // @Operation(summary = "Получение всех факультетов")
////  @GetMapping("/getAllFaculties")
////  public ResponseEntity<List<Faculty>> getAllFaculties() {
////    if (facultyService.getAllFaculties().isEmpty()) {
////      logger.warn("Факультеты не найдены");
////      return ResponseEntity.noContent().build();
////    } else {
////      logger.info("Запрос на получение всех факультетов");
////      return ResponseEntity.ok(facultyService.getAllFaculties());
////    }
////  }
//
//  @DeleteMapping("/{id}")
//  @Operation(summary = "Удаление факультета по ID")
//  public ResponseEntity<Void> deleteFacultyById(@PathVariable @NotNull Long id) {
//    logger.info("Запрос на удаление факультета с ID: {}", id);
//    facultyService.deleteFacultyById(id);
//    return ResponseEntity.ok().build();
//  }
//}


package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school_2.dto.FacultyDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.service.FacultyService;
import ru.hogwarts.school_2.service.StudentService;

@RestController
@Tag(name = "Faculty API", description = "Управление факультетами")
@RequestMapping("/faculty")
@Validated
@Transactional
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

  @PutMapping("/{id}")
  @Operation(summary = "Обновление факультета по ID")
  public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @Valid @RequestBody Faculty faculty) {
    logger.info("Запрос на обновление факультета с ID: {}", id);
    faculty.setId(id);
    Faculty updatedFaculty = facultyService.updateFaculty(faculty);
    return ResponseEntity.ok(updatedFaculty);
  }

  @Operation(summary = "Получение факультета по цвету")
  @GetMapping("/getFacultyByColor")
  public ResponseEntity<Optional<Faculty>> getFacultyByColor(
      @RequestParam("color") @NotBlank String color) {
    logger.info("Запрос на получение факультета по цвету: {}", color);
    Optional<Faculty> faculty = facultyService.getFacultyByColor(color);
    if (faculty.isEmpty()) {
      logger.warn("Факультет с цветом {} не найден", color);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(faculty);
  }

  @Operation(summary = "Получение факультета по имени")
  @GetMapping("/getFacultyByName")
  public ResponseEntity <Optional<Faculty>> getFacultyByName(
      @RequestParam("name") @NotBlank String name) {
    logger.info("Запрос на получение факультета по имени: {}", name);
    Optional<Faculty> faculty = facultyService.getFacultyByName(name);
       if (faculty.isEmpty()){
      logger.warn("Факультет с именем {} не найден", name);
      return ResponseEntity.notFound().build();
    } else {
      logger.info("Запрос на получение факультета по имени: {}", name);
      return ResponseEntity.ok(faculty);
    }
  }

  @Operation(summary = "Получение факультета по ID")
  @GetMapping("/getFacultyById")
  public ResponseEntity<Optional<Faculty>> getFacultyById(@RequestParam @NotNull Long id) {
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
  public ResponseEntity<List<Faculty>> searchFaculties(@Valid
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

  @Operation(summary = "Получение факультета по ID студента")
  @GetMapping("/getFacultyByStudentId")
  public ResponseEntity<FacultyDTO> getFacultyByStudentId(
      @RequestParam @NotNull Long id) {
    logger.info("Запрос на получение факультета по ID студента: {}", id);
    try {
      FacultyDTO facultyDTO = facultyService.getFacultyByStudentId(id);
      logger.info("Факультет студента с ID {} получен: {}", id, facultyDTO.getName());
      return ResponseEntity.ok(facultyDTO);
    } catch (EntityNotFoundException e) {
      logger.warn("Студент с ID {} не найден", id);
      return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      logger.warn("У студента с ID {} нет факультета", id);
      return ResponseEntity.badRequest().body(new FacultyDTO(null, "N/A", "N/A"));
    }
  }

  @Operation(summary = "Получение всех факультетов")
  @GetMapping("/getAllFaculties")
  public ResponseEntity<List<Faculty>> getAllFaculties() {
    if (facultyService.getAllFaculties().isEmpty()) {
      logger.warn("Факультеты не найдены");
      return ResponseEntity.noContent().build();
    } else {
      logger.info("Запрос на получение всех факультетов");
      return ResponseEntity.ok(facultyService.getAllFaculties());
    }
  }

  @Operation(summary = "Удаление факультета по ID")
  @DeleteMapping("/deleteFacultyById")
  public ResponseEntity<String> deleteFacultyById(@NotNull@RequestParam Long id) {
    if (facultyService.getFacultyById(id).isEmpty()) {
      logger.warn("Факультет с ID {} не найден", id);
      return ResponseEntity.notFound().build();
    } else {
      facultyService.deleteFacultyById(id);
      logger.info("Факультет с ID {} успешно удален", id);
    }
    return ResponseEntity.ok("Факультет успешно удален");
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
    logger.error("Ошибка при обработке запроса: {}", e.getMessage());
    return ResponseEntity.badRequest().body(e.getMessage());
  }

}//class
