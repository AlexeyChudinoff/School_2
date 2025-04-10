package ru.hogwarts.school_2.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;

public interface FacultyRepositories extends JpaRepository<Faculty, Long> {

  List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase
      (String name, String color);
  Faculty findByNameIgnoreCase(String name);
  Faculty findByColorIgnoreCase(String color);


}//class