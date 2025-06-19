package ru.hogwarts.school_2.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school_2.dto.FacultyDTO;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class FacultyService {

  private final FacultyRepository facultyRepository;
  private final StudentRepository studentRepository;
  private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
  private StudentService studentService;

  @Autowired
  public FacultyService(FacultyRepository facultyRepository,
      StudentRepository studentRepository,
      StudentService studentService) {
    this.facultyRepository = facultyRepository;
    this.studentRepository = studentRepository;
    this.studentService = studentService;
  }

  @Transactional
  public Faculty addFaculty(Faculty faculty) {
    logger.info("Was invoked method for add faculty");

    logger.debug("Attempting to add faculty: name={}, color={}", faculty.getName(), faculty.getColor());
    if (facultyRepository.findByNameIgnoreCase(faculty.getName()).isPresent()) {
      logger.error("Faculty with name {} already exists", faculty.getName());
      throw new IllegalStateException("Факультет с таким названием уже существует");
    }

    Faculty savedFaculty = facultyRepository.save(faculty);
    logger.debug("Faculty added successfully with ID: {}", savedFaculty.getId());
    return savedFaculty;
  }

  @Transactional
  public Faculty updateFaculty(Faculty faculty) {
    logger.info("Was invoked method for update faculty with ID: {}", faculty.getId());

    logger.debug("Updating faculty: ID={}, name={}, color={}",
        faculty.getId(), faculty.getName(), faculty.getColor());

    if (!facultyRepository.existsById(faculty.getId())) {
      logger.error("Faculty with ID {} not found for update", faculty.getId());
      throw new EntityNotFoundException("Факультет с ID " + faculty.getId() + " не существует");
    }

    facultyRepository.findByNameIgnoreCase(faculty.getName())
        .filter(f -> !f.getId().equals(faculty.getId()))
        .ifPresent(f -> {
          logger.error("Faculty with name {} already exists (ID conflict)", faculty.getName());
          throw new IllegalStateException(
              "Факультет с названием " + faculty.getName() + " уже существует");
        });

    Faculty updatedFaculty = facultyRepository.save(faculty);
    logger.debug("Faculty with ID {} updated successfully", faculty.getId());
    return updatedFaculty;
  }

  public List<Faculty> findByNameOrColor(String name, String color) {
    logger.info("Was invoked method for find faculties by name or color");
    logger.debug("Search parameters - name: {}, color: {}", name, color);

    if ((name == null || name.isBlank()) && (color == null || color.isBlank())) {
      logger.warn("Both name and color parameters are empty");
      return List.of();
    }

    List<Faculty> result = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    logger.debug("Found {} faculties matching criteria", result.size());
    return result;
  }

  public Optional<Faculty> getFacultyById(long id) {
    logger.info("Was invoked method for get faculty by ID: {}", id);

    Optional<Faculty> faculty = facultyRepository.findById(id);
    if (faculty.isEmpty()) {
      logger.warn("Faculty with ID {} not found", id);
    } else {
      logger.debug("Found faculty: ID={}, name={}", id, faculty.get().getName());
    }
    return faculty;
  }

  public Optional<Faculty> getFacultyByName(String name) {
    logger.info("Was invoked method for get faculty by name: {}", name);

    Optional<Faculty> faculty = facultyRepository.findByNameIgnoreCase(name);
    if (faculty.isEmpty()) {
      logger.warn("Faculty with name {} not found", name);
    } else {
      logger.debug("Found faculty: name={}, ID={}", name, faculty.get().getId());
    }
    return faculty;
  }

  public Optional<Faculty> getFacultyByColor(String color) {
    logger.info("Was invoked method for get faculty by color: {}", color);

    Optional<Faculty> faculty = facultyRepository.findByColorIgnoreCase(color);
    if (faculty.isEmpty()) {
      logger.warn("Faculty with color {} not found", color);
    } else {
      logger.debug("Found faculty: color={}, ID={}", color, faculty.get().getId());
    }
    return faculty;
  }

  public List<Faculty> getAllFaculties() {
    logger.info("Was invoked method for get all faculties");

    List<Faculty> faculties = facultyRepository.findAll();
    logger.debug("Retrieved {} faculties from database", faculties.size());
    return faculties;
  }

  public FacultyDTO getFacultyByStudentId(Long id) {
    logger.info("Was invoked method for get faculty by student ID: {}", id);

    logger.debug("Retrieving student with ID: {}", id);
    StudentDTO studentDTO = StudentDTO.fromStudent(studentService.getStudentById(id)
        .orElseThrow(() -> {
          logger.error("Student with ID {} not found", id);
          return new EntityNotFoundException("Студент с ID " + id + " не найден");
        }));

    if (studentDTO.getFacultyId() == null) {
      logger.error("Student with ID {} has no faculty assigned", id);
      throw new IllegalStateException("У студента с ID " + id + " нет назначенного факультета");
    }

    logger.debug("Retrieving faculty with ID: {}", studentDTO.getFacultyId());
    Faculty faculty = facultyRepository.findById(studentDTO.getFacultyId())
        .orElseThrow(() -> {
          logger.error("Faculty with ID {} not found", studentDTO.getFacultyId());
          return new EntityNotFoundException("Факультет не найден");
        });

    logger.debug("Creating FacultyDTO for faculty ID: {}", faculty.getId());
    return new FacultyDTO(faculty.getId(), faculty.getName(), faculty.getColor());
  }

  @Transactional
  public void deleteFacultyById(long id) {
    logger.info("Was invoked method for delete faculty by ID: {}", id);

    if (!facultyRepository.existsById(id)) {
      logger.error("Faculty with ID {} not found for deletion", id);
      throw new EntityNotFoundException("Факультет с ID " + id + " не существует");
    }

    logger.debug("Deleting all students from faculty with ID: {}", id);
    studentService.deleteAllStudentsFromFaculty(id);

    facultyRepository.deleteById(id);
    logger.debug("Faculty with ID {} deleted successfully", id);
  }
}//class