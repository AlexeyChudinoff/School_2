
package ru.hogwarts.school_2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school_2.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepositories extends JpaRepository<Student, Long> {

  // Для уникальных запросов (возвращает Optional)
  Optional<Student> findById(Long id);
  Optional<Student> findByNameAndAge(String name, int age);
  // Для не уникальных запросов (возвращает List)
  List<Student> findByName(String name);
  List<Student> findByAge(int age);
  List<Student> findByAgeBetween(int min, int max);
  List<Student> findByNameContainingIgnoreCase(String name);

  // Стандартные методы JpaRepository:
  // save(), deleteById(), findAll() и другие уже включены автоматически
}