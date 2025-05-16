package ru.hogwarts.school_2.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school_2.dto.StudentDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.AvatarRepository;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;

@Service
//@Transactional(readOnly = true)
public class StudentService {

  private FacultyRepository facultyRepository;
  private StudentRepository studentRepository;
  private AvatarRepository avatarRepository;
  // private StudentDTO studentDTO;

  public StudentService() {
  }

  @Autowired
  public StudentService(
      StudentRepository studentRepository,
      FacultyRepository facultyRepository,
      AvatarRepository avatarRepository) {
    this.studentRepository = studentRepository;
    this.facultyRepository = facultyRepository;
    this.avatarRepository = avatarRepository;

  }

  // Создание студента
  @Transactional
  public Student addStudent(StudentDTO studentDTO, Long facultyId) {
    if (studentRepository.findByNameIgnoreCase(studentDTO.getName()).isEmpty()) {
      Student student = new Student(studentDTO.getName(), studentDTO.getAge(),
          studentDTO.getGender());
      // Используем facultyId из параметра метода, а не из DTO
      facultyRepository.findById(facultyId).ifPresent(student::setFaculty);
      return studentRepository.save(student);
    }
    throw new IllegalStateException("Студент с таким именем уже существует!");
  }

  @Transactional
  public Student updateStudent(Long id, StudentDTO studentDTO) throws NotFoundException {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException());

    student.setName(studentDTO.getName());
    student.setAge(studentDTO.getAge());
    student.setGender(studentDTO.getGender());

    // Определяем новую принадлежность к факультету
    if (studentDTO.getFacultyId() != null) {
      Faculty faculty = facultyRepository.findById(studentDTO.getFacultyId())
          .orElseThrow(() -> new NotFoundException());
      student.setFaculty(faculty);
    }

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

  // Получение студентов одного пола
  public List<Student> findByGender(String gender) {
    return studentRepository.findByGenderIgnoreCase(gender);
  }

  // Получение студентов одного возраста
  public List<Student> getStudentByAge(int age) {
    return studentRepository.findByAge(age);
  }

  // Получение студентов по возрастному диапазону
  public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
    return studentRepository.findByAgeBetween(minAge, maxAge);
  }

  //Получить количество студентов по ID факультета
  public long getCountStudents(Long facultyId) {
    return studentRepository.countByFaculty_Id(facultyId);
  }

  //получение всех студентов по ID факульта
  public List<Student> findAllByFaculty_Id(Long facultyId) {
    return studentRepository.findAllByFaculty_Id(facultyId);
  }

  // Удалить студента по ID
   @Transactional
  public boolean deleteStudentById(Long id) {
    if (!studentRepository.existsById(id)) {
      return false; // Студент не найден
    }

    // Удаляем связанный аватар
    avatarRepository.deleteByStudentId(id);

    // Удаляем студента
    studentRepository.deleteById(id);

    return true; // Студент успешно удалён
  }

  //получить факультет по ID
  public Optional<Faculty> getFacultyById(Long facultyId) {
    return facultyRepository.findById(facultyId);
  }

  //sql

  //удалить всех студентов факультета
  @Transactional
  public void deleteAllStudentsFromFaculty(Long facultyId) {
    List<Student> students = studentRepository.findAllByFaculty_Id(facultyId);
    if (!students.isEmpty()) {
      for (Student student : students) {
        avatarRepository.deleteByStudentId(student.getId());
      }
      studentRepository.deleteAllByFaculty_Id(facultyId);
    }
  }

//получить количество всех студентов
  public long getCountByAllStudens() {
    return studentRepository.getCountByAllStudens();
  }

  // Получить средний возраст студентов
  public Double findAverageAge() {
    return studentRepository.findAverageAge();
  }

  //получаем 5 последних по айди студентов
public List<Student> findTop5ByOrderByIdDesc(){
    return studentRepository.findTop5ByOrderByIdDesc();
  }


}//
