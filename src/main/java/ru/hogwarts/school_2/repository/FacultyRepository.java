package ru.hogwarts.school_2.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school_2.model.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

  List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase
      (String name, String color);
  Optional<Faculty> findByNameIgnoreCase(String name);
  Optional<Faculty> findById(Long id);
  Optional<Faculty> findByColorIgnoreCase(String color);


}//class