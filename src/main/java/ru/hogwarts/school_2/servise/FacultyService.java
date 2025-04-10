package ru.hogwarts.school_2.servise;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.repositories.FacultyRepositories;

@Service
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
        (faculty.getName()) == null) {
      return facultyRepositories.save(faculty);
    } else {
      throw new IllegalStateException("Факультет с таким названием"
          + " уже существует");
    }
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
  public Faculty getFacultyByName(String name) {
    if (facultyRepositories.findByNameIgnoreCase(name) != null) {
      return facultyRepositories.findByNameIgnoreCase(name);
    } else {
      throw new IllegalStateException("Факультет с таким названием"
          + " не существует");
    }
  }

  //поиск поиск цвету
  public Faculty getFacultyByColor(String color) {
    if (facultyRepositories.findByColorIgnoreCase(color) != null) {
      return facultyRepositories.findByColorIgnoreCase(color);
    } else {
      throw new IllegalStateException("Факультет с таким названием"
          + " не существует");
    }
  }


}//class