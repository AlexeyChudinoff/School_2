package ru.hogwarts.school_2.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

@Service
//@Transactional(readOnly = true)
public class StudentService {

  private FacultyRepository facultyRepository;
  private FacultyService facultyService;
  private StudentRepository studentRepository;
  // private StudentDTO studentDTO;

  public StudentService() {
  }

  @Autowired
  public StudentService(StudentRepository studentRepository ) {
    this.studentRepository = studentRepository;

  }

  // Создание студента
  @Transactional
  public Student addStudent(StudentDTO studentDTO, Long facultyId) {
    if (studentRepository.findByNameIgnoreCase(studentDTO.getName()).isEmpty()) {
      Student student = new Student(studentDTO.getName(), studentDTO.getAge(),
          studentDTO.getGender());
      if (studentDTO.getFacultyId() != null) {
        facultyRepository.findById(studentDTO.getFacultyId()).ifPresent(student::setFaculty);
        return studentRepository.save(student);
      }
    }
   throw new IllegalStateException("Студент с таким именем уже существует!");
  }

  // Обновление студента
  public Student updateStudent(StudentDTO studentDTO) {
    Student student = studentRepository.findById(studentDTO.getId()).orElse(null);
    student.setName(studentDTO.getName());
    student.setAge(studentDTO.getAge());
    student.setGender(studentDTO.getGender());
    student.setFaculty(facultyRepository.findById(studentDTO.getFacultyId()).orElse(null));
    return studentRepository.save(student);
  }
  // Получение всех студентов
  public List<StudentDTO> getAllStudents() {
    return studentRepository.findAll().stream()
        .map(student -> new StudentDTO(
            student.getId(),
            student.getName(),
            student.getAge(),
            student.getGender(),
            student.getFaculty() != null ? student.getFaculty().getId() : null
        ))
        .collect(Collectors.toList());
  }

  // Получение студента по id
  public Optional<Student> getStudentById(Long id) {
    return studentRepository.findById(id);
  }
  //Поиск студента по части имени игнорируя регистр
  public List<Student> findByNameContainingIgnoreCase(String name) {
    return studentRepository.findByNameContainingIgnoreCase(name);
  }
  // Получение студентов одного возраста
  public List<Student> getStudentByAge(int age) {
    return studentRepository.findByAge(age);
  }
  // Получение студентов по возрастному диапазону
  public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
    return studentRepository.findByAgeBetween(minAge, maxAge);
  }
  // Получить средний возраст студентов
  public Double findAverageAge() {
    return studentRepository.findAverageAge();
  }
  //Получить количество студентов по ID факультета
  public int getCountStudents(Long facultyId) {
    return studentRepository.countByFaculty_Id(facultyId);
  }

  //получение всех студентов по ID факульта
  public List<Student> findAllByFaculty_Id(Long facultyId) {
    return studentRepository.findAllByFaculty_Id(facultyId);
  }

  public Optional<Student> deleteStudentById(Long id) {
    return studentRepository.deleteStudentById(id);
  }

  //удалить всех студентов факультета
  public void deleteAllStudentsFromFaculty(Long facultyId) {
    if (facultyRepository.existsById(facultyId)) {
      studentRepository.deleteAllByFaculty_Id(facultyId);
    }
  }

  public Optional<Faculty> getFacultyById(Long facultyId) {
    return facultyRepository.findById(facultyId);
  }


}//
