package ru.hogwarts.school_2.repository;

import io.micrometer.common.lang.Nullable;
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

  long countByFaculty_Id(Long facultyId);

  // Для не уникальных запросов (возвращает List)
  List<Student> findByNameIgnoreCase(String name);

  List<Student> findByAge(int age);

  List<Student> findByAgeBetween(int min, int max);

  List<Student> findByNameContainingIgnoreCase(String name);

  List<Student> findByGenderIgnoreCase(String gender);

  List<Student> findAllByFaculty_Id(Long facultyId);

  //SQL запросы

  // Средний возраст студентов
  @Query(value = "SELECT AVG(age) AS average_age FROM Student" , nativeQuery = true)
  @Nullable //Теперь служба автоматически обработает случай, когда среднее значение отсутствует (NULL)
  Double findAverageAge();

  //Удалить всех студентов факультета
  @Modifying//это про то что мы вносим изменения в базу
  @Query(value = "DELETE FROM Student s WHERE s.faculty.id = :facultyId")
  void deleteAllByFaculty_Id(
      @Param("facultyId") Long facultyId); // Здесь ставим void тк после удаления возвращать нечего

  // Получить количество всех студентов школы
  @Query(value = "SELECT COUNT(*) FROM Student", nativeQuery = true)
  long getCountByAllStudens();

  //Получить 5 последних  по id
  @Query(value = "SELECT * FROM Student ORDER BY id DESC LIMIT 5 ", nativeQuery = true)
  List<Student> findTop5ByOrderByIdDesc();

}//
