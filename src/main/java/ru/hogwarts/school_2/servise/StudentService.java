package ru.hogwarts.school_2.servise;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repositories.FacultyRepositories;
import ru.hogwarts.school_2.repositories.StudentRepositories;

@Service
public class StudentService {

  private StudentRepositories studentRepositories;
  private FacultyRepositories facultyRepositories;

  public StudentService() {
  }

  @Autowired
  public StudentService(StudentRepositories studentRepositories,
      FacultyRepositories facultyRepositories) {
    this.studentRepositories = studentRepositories;
    this.facultyRepositories = facultyRepositories;
  }

  // Создание/добавление студента
  public Student addStudent(Student student) {
    return studentRepositories.save(student);
  }

//Поиск студента по части имени игнорируя регистр
  public List<Student> findByNameContainingIgnoreCase(String name) {
    return studentRepositories.findByNameContainingIgnoreCase(name);
  }

  // Получение студентов по возрасту
  public List<Student> getStudentByAge(int age) {
    return studentRepositories.findByAge(age);
  }

  // Получение студентов по id
  public Optional<Student> getStudentById(Long id) {
    return studentRepositories.findById(id);
  }

  // Получение студентов по возрастному диапазону
  public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
    return studentRepositories.findByAgeBetween(minAge, maxAge);
  }

  // Получение всех студентов
  public List<Student> getAllStudents() {
    return studentRepositories.findAll();
  }

  // Обновление студента
  public Student updateStudent(Long id, Student student) {
    student.setId(id); // Устанавливаем ID для обновления существующей записи
    return studentRepositories.save(student);
  }

  // Удаление студента по ID
  public void deleteStudent(Long id) {
    studentRepositories.deleteById(id);
  }
}//
