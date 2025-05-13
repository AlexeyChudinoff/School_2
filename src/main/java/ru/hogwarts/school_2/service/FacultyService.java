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


  //добавление факультета
  @Transactional
  public Faculty addFaculty(Faculty faculty) {
    logger.debug("Добавление факультета: {}", faculty.getName());
    if (facultyRepository.findByNameIgnoreCase(faculty.getName()).isPresent()) {
      throw new IllegalStateException("Факультет с таким названием уже существует");
    }
    return facultyRepository.save(faculty);
  }


  //обновление факультета
  @Transactional
  public Faculty updateFaculty(Faculty faculty) {
    logger.debug("Обновление факультета с ID: {}", faculty.getId());
    if (!facultyRepository.existsById(faculty.getId())) {
      throw new EntityNotFoundException("Факультет с ID " + faculty.getId() + " не существует");
    }

    facultyRepository.findByNameIgnoreCase(faculty.getName())
        .filter(f -> !f.getId().equals(faculty.getId()))
        .ifPresent(f -> {
          throw new IllegalStateException(
              "Факультет с названием " + faculty.getName() + " уже существует");
        });

    return facultyRepository.save(faculty);
  }

  //поиск факультетов по имени или цвету
  public List<Faculty> findByNameOrColor(String name, String color) {
    logger.debug("Поиск факультетов по имени: {} или цвету: {}", name, color);
    if ((name == null || name.isBlank()) && (color == null || color.isBlank())) {
      return List.of();
    }
    return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
  }

  //получение факультета по ID
  public Optional<Faculty> getFacultyById(long id) {
    logger.debug("Получение факультета по ID: {}", id);
    return facultyRepository.findById(id);
  }

  //получение факультета по имени
  public Optional<Faculty> getFacultyByName(String name) {
    logger.debug("Получение факультета по имени: {}", name);
    return facultyRepository.findByNameIgnoreCase(name);
  }

  //получение факультета по цвету
  public Optional<Faculty> getFacultyByColor(String color) {
    logger.debug("Получение факультета по цвету: {}", color);
    return facultyRepository.findByColorIgnoreCase(color);
  }

  //получение всех факультетов
  public List<Faculty> getAllFaculties() {
    return facultyRepository.findAll();
  }

  //Получение факультета по ID студента
  public FacultyDTO getFacultyByStudentId(Long id) {
    logger.debug("Получение факультета по ID студента: {}", id);

    // _Получаем StudentDTO через сервис
    StudentDTO studentDTO = StudentDTO.fromStudent(studentService.getStudentById(id)
        .orElseThrow(() -> new EntityNotFoundException("Студент с ID " + id + " не найден")));

    // _Проверяем, что у студента есть факультет
    if (studentDTO.getFacultyId() == null) {
      throw new IllegalStateException("У студента с ID " + id + " нет назначенного факультета");
    }

    // _Получаем факультет из репозитория
    Faculty faculty = facultyRepository.findById(studentDTO.getFacultyId())
        .orElseThrow(() -> new EntityNotFoundException("Факультет не найден"));

    // _создаем FacultyDTO
    return new FacultyDTO(faculty.getId(), faculty.getName(), faculty.getColor());
  }

  //удаление факультета по ID
  @Transactional
  public void deleteFacultyById(long id) {
    logger.debug("Удаление факультета с ID: {}", id);
    if (!facultyRepository.existsById(id)) {
      throw new EntityNotFoundException("Факультет с ID " + id + " не существует");
    }
    studentService.deleteAllStudentsFromFaculty(id);
    facultyRepository.deleteById(id);
  }

}//
