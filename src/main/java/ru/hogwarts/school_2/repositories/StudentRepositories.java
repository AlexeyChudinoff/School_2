
package ru.hogwarts.school_2.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school_2.model.Student;

public interface StudentRepositories extends JpaRepository<Student, Long> {

  // Для уникальных запросов (возвращает Optional)
  Optional<Student> findById(Long id);

  Optional<Student> findByNameAndAge(String name, int age);

  // Для не уникальных запросов (возвращает List)
  List<Student> findByName(String name);

  List<Student> findByAge(int age);

  List<Student> findByAgeBetween(int min, int max);

  List<Student> findByNameContainingIgnoreCase(String name);

  List<Student> deleteAllByFaculty_Id(Long facultyId); // О(Long facultyId);


  // Стандартные методы JpaRepository:
  // save(), deleteById(), findAll() и другие уже включены автоматически
}