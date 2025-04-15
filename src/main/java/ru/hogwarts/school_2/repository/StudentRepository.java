
package ru.hogwarts.school_2.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school_2.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

  // Для уникальных запросов (возвращает Optional)
  Optional<Student> findById(Long id);

  Optional<Student> findByNameAndAge(String name, int age);

  Optional<Student> deleteStudentById(Long id);

  // Для не уникальных запросов (возвращает List)
  List<Student> findByNameIgnoreCase(String name);

  List<Student> findByAge(int age);

  List<Student> findByAgeBetween(int min, int max);

  List<Student> findByNameContainingIgnoreCase(String name);

  List<Student> deleteAllByFaculty_Id(Long facultyId);

  List<Student> findAllByFaculty_Id(Long facultyId);

  @Query("SELECT AVG(s.age) FROM Student s")
  Double findAverageAge();;// средний возраст студентов

  int countByFaculty_Id(Long facultyId);// количество студентов факультета

//  List<Student> findAll ();

  // Стандартные методы JpaRepository:
  // save(), deleteById(), findAll() и другие уже включены автоматически
}