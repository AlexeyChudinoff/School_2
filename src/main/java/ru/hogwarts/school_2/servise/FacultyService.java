package ru.hogwarts.school_2.servise;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school_2.dto.FacultyDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repositories.FacultyRepositories;

@Service
@Transactional(readOnly = true)
public class FacultyService {

  private FacultyRepositories facultyRepositories;
  private StudentService studentService;
  private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

  public FacultyService() {
  }

  @Autowired
  public FacultyService(FacultyRepositories facultyRepositories,
      StudentService studentService) {
    this.facultyRepositories = facultyRepositories;
    this.studentService = studentService;
  }


  //Добавить/создать факультет
  public Faculty addFaculty(Faculty faculty) {
    if (facultyRepositories.findByNameIgnoreCase
        (faculty.getName()).isEmpty()) {
      return facultyRepositories.save(faculty);
    } else {
      throw new IllegalStateException("Факультет с таким названием"
          + " уже существует");
    }
  }

  //изменение факультета
  public Faculty updateFaculty(Faculty faculty) {
    Long id = faculty.getId();
    logger.debug("Попытка обновления факультета с ID: {}", id);
    Faculty existingFaculty = facultyRepositories.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Факультет с ID " + id + " не существует"));
    if (!existingFaculty.getName().equals(faculty.getName())) {
      if (facultyRepositories.findByNameIgnoreCase(faculty.getName()).isPresent()) {
        throw new IllegalStateException("Факультет с названием " + faculty.getName() +
            " уже существует");
      }
    }
    return facultyRepositories.save(faculty);
  }

  // Поиск по имени или цвету
  public List<Faculty> findByNameOrColor(String name, String color) {
    logger.debug("Поиск факультетов по имени: {} или цвету: {}", name, color);
    if ((name == null || name.isBlank()) && (color == null || color.isBlank())) {
      logger.warn("Попытка поиска с пустыми параметрами имени и цвета");
      return Collections.emptyList();
    }
    return facultyRepositories.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
  }

  // поиск по id
  public Optional<Faculty> getFacultyById(long id) {
    if (facultyRepositories.findById(id).isPresent()) {
      return facultyRepositories.findById(id);
    } else {
      throw new IllegalStateException("Факультет с таким id"
          + " не существует");
    }
  }

  //поиск по имени
  public Optional<Faculty> getFacultyByName(String name) {
    if (facultyRepositories.findByNameIgnoreCase(name).isPresent()) {
      return facultyRepositories.findByNameIgnoreCase(name);
    } else {
      throw new IllegalStateException("Факультет с таким названием"
          + " не существует");
    }
  }

  //поиск цвету
  public Faculty getFacultyByColor(String color) {
    if (facultyRepositories.findByColorIgnoreCase(color) != null) {
      return facultyRepositories.findByColorIgnoreCase(color);
    } else {
      throw new IllegalStateException("Факультет с таким названием"
          + " не существует");
    }
  }

  //поиск факультета по ID студента

  public FacultyDTO getFacultyByStudentId(Long id) {
    return studentService.getStudentById(id)
        .map(Student::getFaculty)
        .map(f -> new FacultyDTO(f.getId(), f.getName(), f.getColor()))
        .orElseThrow(() -> new EntityNotFoundException("Студент или факультет не найден"));
  }

//  public Faculty getFacultyByStudentId(Long id) {
//    Optional<Student> studentOpt = studentService.getStudentById(id); // Получаем студента
//    if (studentOpt.isEmpty()) { // Если студента нет
//      throw new EntityNotFoundException("Студент с указанным ID не найден");
//    }
//    Student student = studentOpt.get(); // Извлекаем студента
//    Faculty faculty = student.getFaculty(); // Берём его факультет
//    if (faculty == null) {
//      throw new IllegalStateException("У студента нет назначенного факультета");
//    }
//    return faculty; // Возвращаем найденный факультет
//  }


  //получение всех факультетов
  public List<Faculty> getAllFaculties() {
    return facultyRepositories.findAll();
  }

  //удаление факультета
  public void deleteFacultyById(long id) {
    if (facultyRepositories.findById(id).isPresent()) {
      studentService.deleteAllStudentsFromFaculty(id);
      facultyRepositories.deleteById(id);
    } else {
      throw new IllegalStateException("Факультет с таким id не существует");
    }
  }


}//class