package ru.hogwarts.school_2.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school_2.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

  // Для уникальных запросов (возвращает Optional)
  Optional<Student> findById(Long id);

  Optional<Student> findByNameAndAge(String name, int age);

  // ! Удаляю этот метод, так как он некорректен
  // Optional<Student> deleteStudentById(Long id); <-- НЕТ, удаляем

  // Для не уникальных запросов (возвращает List)
  List<Student> findByNameIgnoreCase(String name);

  List<Student> findByAge(int age);

  List<Student> findByAgeBetween(int min, int max);

  List<Student> findByNameContainingIgnoreCase(String name);

  @Modifying
  @Query("DELETE FROM Student s WHERE s.faculty.id = :facultyId")
  void deleteAllByFaculty_Id(@Param("facultyId") Long facultyId); // Здесь тоже ставим void

  List<Student> findByGenderIgnoreCase(String gender);

  List<Student> findAllByFaculty_Id(Long facultyId);

  @Query("SELECT AVG(s.age) FROM Student s")
  Double findAverageAge(); // Средний возраст студентов

  long countByFaculty_Id(Long facultyId); // Кол-во студентов по факультету (изменил на long)

}

// Стандартные методы JpaRepository:
// save(), deleteById(), findAll() и другие уже включены автоматически