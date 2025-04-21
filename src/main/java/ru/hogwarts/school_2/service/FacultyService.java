package ru.hogwarts.school_2.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//@Service
//@Transactional(readOnly = true)
//public class FacultyService {
//
//  private FacultyRepository facultyRepositories;
//  private StudentService studentService;
//  private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
//
//  public FacultyService() {
//  }
//
//  @Autowired
//  public FacultyService(FacultyRepository facultyRepositories,
//      StudentService studentService) {
//    this.facultyRepositories = facultyRepositories;
//    this.studentService = studentService;
//  }
//
//
//  //Добавить/создать факультет
//  public Faculty addFaculty(Faculty faculty) {
//    if (facultyRepositories.findByNameIgnoreCase
//        (faculty.getName()).isEmpty()) {
//      return facultyRepositories.save(faculty);
//    } else {
//      throw new IllegalStateException("Факультет с таким названием"
//          + " уже существует");
//    }
//  }
//
//  //изменение факультета
//  public Faculty updateFaculty(Faculty faculty) {
//    Long id = faculty.getId();
//    logger.debug("Попытка обновления факультета с ID: {}", id);
//    Faculty existingFaculty = facultyRepositories.findById(id)
//        .orElseThrow(() -> new EntityNotFoundException("Факультет с ID " + id + " не существует"));
//    if (!existingFaculty.getName().equals(faculty.getName())) {
//      if (facultyRepositories.findByNameIgnoreCase(faculty.getName()).isPresent()) {
//        throw new IllegalStateException("Факультет с названием " + faculty.getName() +
//            " уже существует");
//      }
//    }
//    return facultyRepositories.save(faculty);
//  }
//
//  // Поиск по имени или цвету
//  public List<Faculty> findByNameOrColor(String name, String color) {
//    logger.debug("Поиск факультетов по имени: {} или цвету: {}", name, color);
//    if ((name == null || name.isBlank()) && (color == null || color.isBlank())) {
//      logger.warn("Попытка поиска с пустыми параметрами имени и цвета");
//      return Collections.emptyList();
//    }
//    return facultyRepositories.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
//  }
//
//  // поиск по id
//  public Optional<Faculty> getFacultyById(long id) {
//    if (facultyRepositories.findById(id).isPresent()) {
//      return facultyRepositories.findById(id);
//    } else {
//      throw new IllegalStateException("Факультет с таким id"
//          + " не существует");
//    }
//  }
//
//  //поиск по имени
//  public Optional<Faculty> getFacultyByName(String name) {
//    if (facultyRepositories.findByNameIgnoreCase(name).isPresent()) {
//      return facultyRepositories.findByNameIgnoreCase(name);
//    } else {
//      throw new IllegalStateException("Факультет с таким названием"
//          + " не существует");
//    }
//  }
//
//  //поиск цвету
//  public Faculty getFacultyByColor(String color) {
//    if (facultyRepositories.findByColorIgnoreCase(color) != null) {
//      return facultyRepositories.findByColorIgnoreCase(color);
//    } else {
//      throw new IllegalStateException("Факультет с таким названием"
//          + " не существует");
//    }
//  }
//
//  //поиск факультета по ID студента
//
//  public FacultyDTO getFacultyByStudentId(Long id) {
//    return studentService.getStudentById(id)
//        .map(Student::getFaculty)
//        .map(f -> new FacultyDTO(f.getId(), f.getName(), f.getColor()))
//        .orElseThrow(() -> new EntityNotFoundException("Студент или факультет не найден"));
//  }
//
////  public Faculty getFacultyByStudentId(Long id) {
////    Optional<Student> studentOpt = studentService.getStudentById(id); // Получаем студента
////    if (studentOpt.isEmpty()) { // Если студента нет
////      throw new EntityNotFoundException("Студент с указанным ID не найден");
////    }
////    Student student = studentOpt.get(); // Извлекаем студента
////    Faculty faculty = student.getFaculty(); // Берём его факультет
////    if (faculty == null) {
////      throw new IllegalStateException("У студента нет назначенного факультета");
////    }
////    return faculty; // Возвращаем найденный факультет
////  }
//
//
//  //получение всех факультетов
//  public List<Faculty> getAllFaculties() {
//    return facultyRepositories.findAll();
//  }
//
//  //удаление факультета
//  public void deleteFacultyById(long id) {
//    if (facultyRepositories.findById(id).isPresent()) {
//      studentService.deleteAllStudentsFromFaculty(id);
//      facultyRepositories.deleteById(id);
//    } else {
//      throw new IllegalStateException("Факультет с таким id не существует");
//    }
//  }
//
//
//}//class