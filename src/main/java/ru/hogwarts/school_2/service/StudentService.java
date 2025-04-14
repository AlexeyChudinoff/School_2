package ru.hogwarts.school_2.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

  private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
  private final StudentRepository studentRepository;
  private final FacultyRepository facultyRepository;

  @Transactional
  public StudentDTO addStudent(StudentDTO studentDTO) {
    logger.debug("Добавление студента: {}", studentDTO.getName());
    if (!studentRepository.findByNameIgnoreCase(studentDTO.getName()).isEmpty()) {
      throw new IllegalStateException("Студент с таким именем уже существует");
    }

    Student student = new Student();
    student.setName(studentDTO.getName());
    student.setAge(studentDTO.getAge());
    student.setGender(studentDTO.getGender());

    if (studentDTO.getFacultyId() != null) {
      facultyRepository.findById(studentDTO.getFacultyId())
          .ifPresent(student::setFaculty);
    }

    Student savedStudent = studentRepository.save(student);
    return StudentDTO.fromStudent(savedStudent);
  }

  @Transactional
  public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
    logger.debug("Обновление студента с ID: {}", id);
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException
            ("Студент с ID " + id + " не найден"));

    student.setName(studentDTO.getName());
    student.setAge(studentDTO.getAge());
    student.setGender(studentDTO.getGender());

    if (studentDTO.getFacultyId() != null) {
      facultyRepository.findById(studentDTO.getFacultyId())
          .ifPresent(student::setFaculty);
    } else {
      student.setFaculty(null);
    }

    Student updatedStudent = studentRepository.save(student);
    return StudentDTO.fromStudent(updatedStudent);
  }

  public Optional<StudentDTO> getStudentById(Long id) {
    logger.debug("Получение студента по ID: {}", id);
    return studentRepository.findById(id)
        .map(StudentDTO::fromStudent);
  }

  public List<StudentDTO> getStudentByAge(int age) {
    logger.debug("Получение студентов по возрасту: {}", age);
    return studentRepository.findByAge(age).stream()
        .map(StudentDTO::fromStudent)
        .collect(Collectors.toList());
  }

  public List<StudentDTO> getStudentsByFaculty(Long facultyId) {
    logger.debug("Получение студентов по факультету с ID: {}", facultyId);
    return studentRepository.findAllByFaculty_Id(facultyId).stream()
        .map(StudentDTO::fromStudent)
        .collect(Collectors.toList());
  }

  public List<StudentDTO> getStudentsByAgeRange(int minAge, int maxAge) {
    logger.debug("Получение студентов по возрастному диапазону: {} - {}", minAge, maxAge);
    return studentRepository.findByAgeBetween(minAge, maxAge).stream()
        .map(StudentDTO::fromStudent)
        .collect(Collectors.toList());
  }

  public List<StudentDTO> getAllStudents() {
    logger.debug("Получение всех студентов");
    return studentRepository.findAll().stream()
        .map(StudentDTO::fromStudent)
        .collect(Collectors.toList());
  }

  @Transactional
  public void deleteStudent(Long id) {
    logger.debug("Удаление студента с ID: {}", id);
    if (!studentRepository.existsById(id)) {
      throw new EntityNotFoundException("Студент с ID " + id + " не найден");
    }
    studentRepository.deleteById(id);
  }

  @Transactional
  public void deleteAllStudentsFromFaculty(Long facultyId) {
    logger.debug("Удаление всех студентов с факультета ID: {}", facultyId);
    studentRepository.deleteAllByFaculty_Id(facultyId);
  }


}


//package ru.hogwarts.school_2.service;
//
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import ru.hogwarts.school_2.dto.StudentDTO;
//import ru.hogwarts.school_2.model.Student;
//import ru.hogwarts.school_2.repositories.FacultyRepositories;
//import ru.hogwarts.school_2.repositories.StudentRepositories;
//
//@Service
//@Transactional(readOnly = true)
//public class StudentService {
//
//  private StudentRepositories studentRepositories;
//  private FacultyRepositories facultyRepositories;
//  private StudentDTO studentDTO;
//
//  public StudentService() {
//  }
//
//  @Autowired
//  public StudentService(StudentRepositories studentRepositories,
//      FacultyRepositories facultyRepositories) {
//    this.studentRepositories = studentRepositories;
//    this.facultyRepositories = facultyRepositories;
//  }
//
//  // Создание/добавление студента
//  public Student addStudent(StudentDTO studentDTO) {
//    if (studentRepositories.findByNameIgnoreCase(studentDTO.getName()).isEmpty()) {
//      Student student = new Student(studentDTO.getName(), studentDTO.getAge(),
//          studentDTO.getGender());
//      if (studentDTO.getFacultyId() != null) {
//        facultyRepositories.findById(studentDTO.getFacultyId()).ifPresent(student::setFaculty);
//      }
//      return studentRepositories.save(student);
//    } else {
//      throw new IllegalStateException("Студент с таким именем уже существует");
//    }
//  }
//
//  // Обновление студента
//  public Student updateStudent(Long id, StudentDTO studentDTO) {
//    studentRepositories.findById(id)
//        .orElseThrow(() -> new IllegalStateException("Студент не найден"));
//    Student student = studentRepositories.findById(id).orElse(null);
//    student.setName(studentDTO.getName());
//    student.setAge(studentDTO.getAge());
//    student.setGender(studentDTO.getGender());
//    student.setFaculty(facultyRepositories.findById(studentDTO.getFacultyId()).orElse(null));
//    return studentRepositories.save(student);
//
//  }
//
//  //Поиск студента по части имени игнорируя регистр
//  public List<Student> findByNameContainingIgnoreCase(String name) {
//    return studentRepositories.findByNameContainingIgnoreCase(name);
//  }
//
//  // Получение студентов по возрасту
//  public List<Student> getStudentByAge(int age) {
//    return studentRepositories.findByAge(age);
//  }
//
//  // Получение студента по id
//  public Optional<Student> getStudentById(Long id) {
//    return studentRepositories.findById(id);
//  }
//
//  //получение всех студентов по ID факульта
//  public List<Student> getStudentsByFaculty(Long facultyId) {
//    return studentRepositories.getAllByFaculty_Id(facultyId);
//  }
//
//
//  // Получение студентов по возрастному диапазону
//  public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
//    return studentRepositories.findByAgeBetween(minAge, maxAge);
//  }
//
//
//  // Получение всех студентов
//  public List<StudentDTO> getAllStudents() {
//    return studentRepositories.findAll().stream()
//        .map(student -> new StudentDTO(
//            student.getId(),
//            student.getName(),
//            student.getAge(),
//            student.getGender(),
//            student.getFaculty() != null ? student.getFaculty().getId() : null
//        ))
//        .collect(Collectors.toList());
//  }
//
//  // Удаление студента по ID
//  public void deleteStudent(Long id) {
//    studentRepositories.deleteById(id);
//  }
//
//  //удалить всех студентов факультета
//  public void deleteAllStudentsFromFaculty(Long facultyId) {
//    if (facultyRepositories.existsById(facultyId)) {
//      studentRepositories.deleteAllByFaculty_Id(facultyId);
//    }
//  }
//
//}//
