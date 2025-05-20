package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

@RestController
@Tag(name = "Faculty API", description = "Управление факультетами")
@RequestMapping("/faculty")
@Validated
@Transactional
public class FacultyController {

  private final FacultyService facultyService;
  private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);

  @Autowired
  public FacultyController(FacultyService facultyService) {
    this.facultyService = facultyService;
  }

  /**
   * Метод создания факультета.
   *
   * @param faculty информация о факультете
   * @return объект созданного факультета
   */
  @Operation(summary = "Добавление/Создание факультета")
  @PostMapping(value = "/addFaculty", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Faculty> createFaculty(@Valid @RequestBody Faculty faculty) {
    logger.info("Запрос на добавление факультета: {}", faculty.getName());
    Faculty createdFaculty = facultyService.addFaculty(faculty);
    logger.info("Факультет успешно создан: {}", createdFaculty.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
  }

  /**
   * Метод обновления факультета.
   *
   * @param id      идентификатор факультета
   * @param faculty новая информация о факультете
   * @return объект обновленного факультета
   */
  @Operation(summary = "Обновление факультета по ID")
  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id,
      @Valid @RequestBody Faculty faculty) {
    logger.info("Запрос на обновление факультета с ID: {}", id);
    faculty.setId(id);
    Faculty updatedFaculty = facultyService.updateFaculty(faculty);
    return ResponseEntity.ok(updatedFaculty);
  }

  /**
   * Метод получения факультета по цвету.
   *
   * @param color цвет факультета
   * @return найденный факультет
   */
  @Operation(summary = "Получение факультета по цвету")
  @GetMapping(value = "/getFacultyByColor", produces = MediaType.APPLICATION_JSON_VALUE)
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

  /**
   * Метод получения факультета по имени.
   *
   * @param name имя факультета
   * @return найденный факультет
   */
  @Operation(summary = "Получение факультета по имени")
  @GetMapping(value = "/getFacultyByName", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Optional<Faculty>> getFacultyByName(
      @RequestParam("name") @NotBlank String name) {
    logger.info("Запрос на получение факультета по имени: {}", name);
    Optional<Faculty> faculty = facultyService.getFacultyByName(name);
    if (faculty.isEmpty()) {
      logger.warn("Факультет с именем {} не найден", name);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(faculty);
  }

  /**
   * Метод получения факультета по ID.
   *
   * @param id идентификатор факультета
   * @return найденный факультет
   */
  @Operation(summary = "Получение факультета по ID")
  @GetMapping(value = "/getFacultyById", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Optional<Faculty>> getFacultyById(@RequestParam @NotNull Long id) {
    logger.info("Запрос на получение факультета по ID: {}", id);
    Optional<Faculty> faculty = facultyService.getFacultyById(id);
    if (faculty.isEmpty()) {
      logger.warn("Факультет с ID {} не найден", id);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(faculty);
  }

  /**
   * Метод поиска факультетов по имени или цвету.
   *
   * @param name  имя факультета (необязательно)
   * @param color цвет факультета (необязательно)
   * @return список соответствующих факультетов
   */
  @Operation(summary = "Получение списка факультетов по имени или цвету")
  @GetMapping(value = "/searchByNameOrColor", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Faculty>> searchFaculties(@RequestParam(required = false) String name,
      @RequestParam(required = false) String color) {
    logger.info("Запрос на поиск факультетов по имени: {} или цвету: {}", name, color);
    List<Faculty> faculties = facultyService.findByNameOrColor(name, color);
    if (faculties.isEmpty()) {
      logger.warn("Факультеты по указанным критериям не найдены.");
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(faculties);
  }

  /**
   * Метод получения факультета по ID студента.
   *
   * @param id идентификатор студента
   * @return найденный факультет
   */
  @Operation(summary = "Получение факультета по ID студента")
  @GetMapping(value = "/getFacultyByStudentId", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FacultyDTO> getFacultyByStudentId(@RequestParam @NotNull Long id) {
    logger.info("Запрос на получение факультета по ID студента: {}", id);
    try {
      FacultyDTO facultyDTO = facultyService.getFacultyByStudentId(id);
      logger.info("Факультет студента с ID {} получен: {}", id, facultyDTO.getName());
      return ResponseEntity.ok(facultyDTO);
    } catch (EntityNotFoundException e) {
      logger.warn("Студент с ID {} не найден.", id);
      return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      logger.warn("У студента с ID {} отсутствует факультет.", id);
      return ResponseEntity.badRequest().body(new FacultyDTO(null, "N/A", "N/A"));
    }
  }

  /**
   * Метод получения всех факультетов.
   *
   * @return список всех существующих факультетов
   */
  @Operation(summary = "Получение всех факультетов")
  @GetMapping(value = "/getAllFaculties", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Faculty>> getAllFaculties() {
    logger.info("Запрос на получение всех факультетов");
    List<Faculty> allFaculties = facultyService.getAllFaculties();
    if (allFaculties.isEmpty()) {
      logger.warn("Факультеты отсутствуют.");
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(allFaculties);
  }

  /**
   * Метод удаления факультета по ID.
   *
   * @param id идентификатор факультета
   * @return успешный статус удаления
   */
  @Operation(summary = "Удаление факультета по ID")
  @DeleteMapping(value = "/deleteFacultyById", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> deleteFacultyById(@RequestParam @NotNull Long id) {
    logger.info("Запрос на удаление факультета с ID: {}", id);
    if (!facultyService.getFacultyById(id).isPresent()) {
      logger.warn("Факультет с ID {} не найден.", id);
      return ResponseEntity.notFound().build();
    }
    facultyService.deleteFacultyById(id);
    logger.info("Факультет с ID {} успешно удалён.", id);
    return ResponseEntity.ok("Факультет успешно удалён");
  }

  /**
   * Метод обработки исключительных ситуаций.
   *
   * @param exception возникающее исключение
   * @return ошибка Bad Request с описанием
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
    logger.error("Ошибка при обработке запроса: {}", exception.getMessage());
    return ResponseEntity.badRequest().body(exception.getMessage());
  }

}//
