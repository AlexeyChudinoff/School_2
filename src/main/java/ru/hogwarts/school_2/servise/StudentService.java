package ru.hogwarts.school_2.servise;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repositories.FacultyRepositories;
import ru.hogwarts.school_2.repositories.StudentRepositories;

@Service
@Transactional
public class StudentService {

  private StudentRepositories studentRepositories;
  private FacultyRepositories facultyRepositories;
  private StudentDTO studentDTO;

  public StudentService() {
  }

  @Autowired
  public StudentService(StudentRepositories studentRepositories,
      FacultyRepositories facultyRepositories) {
    this.studentRepositories = studentRepositories;
    this.facultyRepositories = facultyRepositories;
  }

  // Создание/добавление студента
  public Student addStudent(StudentDTO studentDTO) {
    if (studentRepositories.findByNameIgnoreCase(studentDTO.getName()).isEmpty()) {
      Student student = new Student(studentDTO.getName(), studentDTO.getAge(), studentDTO.getGender());
      if (studentDTO.getFacultyId() != null) {
        facultyRepositories.findById(studentDTO.getFacultyId()).ifPresent(student::setFaculty);
      }
      return studentRepositories.save(student);
    } else {
      throw new IllegalStateException("Студент с таким именем уже существует");
    }
  }

  // Обновление студента
  public Student updateStudent(Long id, StudentDTO studentDTO) {
   studentRepositories.findById(id).orElseThrow(() -> new IllegalStateException("Студент не найден"));
   Student student = studentRepositories.findById(id).orElse(null);
   student.setName(studentDTO.getName());
   student.setAge(studentDTO.getAge());
   student.setGender(studentDTO.getGender());
   student.setFaculty(facultyRepositories.findById(studentDTO.getFacultyId()).orElse(null));
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

  //получение всех студентов по ID факульта
  public List<Student> getStudentsByFaculty(Long facultyId) {
    return studentRepositories.getAllByFaculty_Id(facultyId);
  }


  // Получение студентов по возрастному диапазону
  public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
    return studentRepositories.findByAgeBetween(minAge, maxAge);
  }

  // Получение всех студентов
  public List<StudentDTO> getAllStudents() {
    return studentRepositories.findAll().stream()
        .map(student -> new StudentDTO(
            student.getId(),
            student.getName(),
            student.getAge(),
            student.getGender(),
            student.getFaculty() != null ? student.getFaculty().getId() : null
        ))
        .collect(Collectors.toList());
  }

  // Удаление студента по ID
  public void deleteStudent(Long id) {
    studentRepositories.deleteById(id);
  }

  //удалить всех студентов факультета
  public void deleteAllStudentsFromFaculty(Long facultyId) {
    if (facultyRepositories.existsById(facultyId)) {
      studentRepositories.deleteAllByFaculty_Id(facultyId);
    }
  }

}//
