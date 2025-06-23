package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school_2.dto.FacultyDTO;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Slf4j
@Tag(name = "Stream API", description = "Операции с использованием Stream API")
@RestController
public class StreamApiController {

  private final StudentRepository studentRepository;
  private final FacultyRepository facultyRepository;

  public StreamApiController(StudentRepository studentRepository,
      FacultyRepository facultyRepository) {
    this.studentRepository = studentRepository;
    this.facultyRepository = facultyRepository;
  }

  @Operation(summary = "Фильтр имён по букве",
      description = "Возвращает имена студентов, начинающиеся с указанной русской буквы")
  @GetMapping("/students/names-starting-with")
  public List<String> getStudentNamesStartingWithLetter(
      @Parameter(description = "Русская буква для фильтрации", example = "А")
      @RequestParam String letter) {

    if (letter == null || letter.length() != 1) {
      throw new IllegalArgumentException("Параметр 'letter' должен быть одной русской буквой");
    }

    char targetLetter = Character.toUpperCase(letter.charAt(0));

    return studentRepository.findAll().stream()
        .map(Student::getName)
        .filter(Objects::nonNull)
        .filter(name -> !name.isEmpty())
        .map(String::toUpperCase)
        .filter(name -> name.charAt(0) == targetLetter)
        .sorted()
        .toList();
  }

  @Operation(summary = "Средний возраст студентов",
      description = "Вычисляет средний возраст всех студентов")
  @GetMapping("/students/average-age2")
  public Double getAverageAgeOfStudents() {
    return studentRepository.findAll().stream()
        .filter(Objects::nonNull)
        .mapToInt(Student::getAge)
        .average()
        .orElse(0.0);
  }

  @Operation(summary = "Факультет с самым длинным названием",
      description = "Возвращает факультет с самым длинным названием")
  @GetMapping("/faculties/longest-name-details")
  public FacultyDTO getFacultyWithLongestName() {
    return facultyRepository.findAll().stream()
        .filter(Objects::nonNull)
        .max(Comparator.comparingInt(f -> f.getName().length()))
        .map(FacultyDTO::fromFaculty)
        .orElse(null);
  }

  @Operation(summary = "Самое длинное название факультета",
      description = "Возвращает только название самого длинного факультета")
  @GetMapping("/faculties/longest-name")
  public String getLongestFacultyName() {
    return facultyRepository.findAll().stream()
        .map(Faculty::getName)
        .filter(Objects::nonNull)
        .max(Comparator.comparingInt(String::length))
        .orElse("Нет факультетов");
  }

  @Operation(summary = "Быстрая сумма (формула)",
      description = "Вычисляет сумму чисел от 1 до 1 000 000 по формуле")
  @GetMapping("/sumFast")
  public ResponseEntity<Long> calculateSumDefinitelyFast() {
    final long startTime = System.nanoTime();
    final long numberLimit = 1_000_000L;
    long result = numberLimit * (numberLimit + 1) / 2;

    return ResponseEntity.ok()
        .header("X-Compute-Time-Ms",
            String.valueOf(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)))
        .body(result);
  }

  @Operation(summary = "Сумма через Stream",
      description = "Вычисляет сумму чисел от 1 до 1 000 000 через параллельный Stream")
  @GetMapping("/sumStream")
  public ResponseEntity<Long> calculateSum() {
    final long startTime = System.nanoTime();
    final long numberLimit = 1_000_000L;

    long result = LongStream.rangeClosed(1, numberLimit)
        .parallel()
        .sum();

    return ResponseEntity.ok()
        .header("X-Compute-Time-Ms",
            String.valueOf(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)))
        .body(result);
  }


}//class