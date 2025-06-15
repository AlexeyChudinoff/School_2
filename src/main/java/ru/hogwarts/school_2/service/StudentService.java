package ru.hogwarts.school_2.service;

import jakarta.persistence.EntityNotFoundException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StudentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudentService.class);

  private FacultyRepository facultyRepository;
  private StudentRepository studentRepository;
  private AvatarRepository avatarRepository;

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

  @Transactional
  public Student addStudent(StudentDTO studentDTO, Long facultyId) {
    LOGGER.info("Was invoked method for add student");

    if (!studentRepository.findByNameIgnoreCase(studentDTO.getName()).isEmpty()) {
      LOGGER.warn("Attempt to create student with existing name: {}", studentDTO.getName());
      throw new IllegalStateException("Студент с таким именем уже существует!");
    }

    Faculty faculty = facultyRepository.findById(facultyId)
        .orElseThrow(() -> {
          LOGGER.error("Faculty with ID {} not found", facultyId);
          return new EntityNotFoundException("Факультет с ID " + facultyId + " не найден");
        });

    Student student = new Student(studentDTO.getName(), studentDTO.getAge(), studentDTO.getGender());
    student.setFaculty(faculty);

    LOGGER.debug("Creating student: name={}, age={}, gender={}, facultyId={}",
        studentDTO.getName(), studentDTO.getAge(), studentDTO.getGender(), facultyId);
    return studentRepository.save(student);
  }

  @Transactional
  public Student updateStudent(Long id, StudentDTO studentDTO) throws NotFoundException {
    LOGGER.info("Was invoked method for update student with id: {}", id);

    Student student = studentRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.error("Student with ID {} not found for update", id);
          return new NotFoundException();
        });

    LOGGER.debug("Updating student with id: {}. New data: name={}, age={}, gender={}",
        id, studentDTO.getName(), studentDTO.getAge(), studentDTO.getGender());

    student.setName(studentDTO.getName());
    student.setAge(studentDTO.getAge());
    student.setGender(studentDTO.getGender());

    if (studentDTO.getFacultyId() != null) {
      Faculty faculty = facultyRepository.findById(studentDTO.getFacultyId())
          .orElseThrow(() -> {
            LOGGER.error("Faculty with ID {} not found during student update", studentDTO.getFacultyId());
            return new NotFoundException();
          });
      student.setFaculty(faculty);
    }

    return studentRepository.save(student);
  }

  public List<StudentDTO> getAllStudents() {
    LOGGER.info("Was invoked method for get all students");
    LOGGER.debug("Fetching all students from database");
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

  public Optional<Student> getStudentById(Long id) {
    LOGGER.info("Was invoked method for get student by id: {}", id);
    Optional<Student> student = studentRepository.findById(id);
    if (student.isEmpty()) {
      LOGGER.warn("Student with ID {} not found", id);
    }
    return student;
  }

  public List<Student> findByNameContainingIgnoreCase(String name) {
    LOGGER.info("Was invoked method for find students by name containing: {}", name);
    LOGGER.debug("Searching students with name containing: {}", name);
    return studentRepository.findByNameContainingIgnoreCase(name);
  }

  public List<Student> findByGender(String gender) {
    LOGGER.info("Was invoked method for find students by gender: {}", gender);
    LOGGER.debug("Searching students with gender: {}", gender);
    return studentRepository.findByGenderIgnoreCase(gender);
  }

  public List<Student> getStudentByAge(int age) {
    LOGGER.info("Was invoked method for get students by age: {}", age);
    LOGGER.debug("Searching students with age: {}", age);
    return studentRepository.findByAge(age);
  }

  public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
    LOGGER.info("Was invoked method for get students by age range: {} - {}", minAge, maxAge);
    LOGGER.debug("Searching students with age between {} and {}", minAge, maxAge);
    return studentRepository.findByAgeBetween(minAge, maxAge);
  }

  public long getCountStudents(Long facultyId) {
    LOGGER.info("Was invoked method for get count of students by faculty id: {}", facultyId);
    long count = studentRepository.countByFaculty_Id(facultyId);
    LOGGER.debug("Found {} students for faculty with ID {}", count, facultyId);
    return count;
  }

  public List<Student> findAllByFaculty_Id(Long facultyId) {
    LOGGER.info("Was invoked method for find all students by faculty id: {}", facultyId);
    List<Student> students = studentRepository.findAllByFaculty_Id(facultyId);
    LOGGER.debug("Found {} students for faculty with ID {}", students.size(), facultyId);
    return students;
  }

  @Transactional
  public boolean deleteStudentById(Long id) {
    LOGGER.info("Was invoked method for delete student by id: {}", id);

    if (!studentRepository.existsById(id)) {
      LOGGER.warn("Attempt to delete non-existing student with ID: {}", id);
      return false;
    }

    LOGGER.debug("Deleting avatar for student with ID: {}", id);
    avatarRepository.deleteByStudentId(id);

    LOGGER.debug("Deleting student with ID: {}", id);
    studentRepository.deleteById(id);

    return true;
  }

  public Optional<Faculty> getFacultyById(Long facultyId) {
    LOGGER.info("Was invoked method for get faculty by id: {}", facultyId);
    Optional<Faculty> faculty = facultyRepository.findById(facultyId);
    if (faculty.isEmpty()) {
      LOGGER.warn("Faculty with ID {} not found", facultyId);
    }
    return faculty;
  }

  @Transactional
  public void deleteAllStudentsFromFaculty(Long facultyId) {
    LOGGER.info("Was invoked method for delete all students from faculty with id: {}", facultyId);

    List<Student> students = studentRepository.findAllByFaculty_Id(facultyId);
    if (students.isEmpty()) {
      LOGGER.warn("No students found for faculty with ID: {}", facultyId);
      return;
    }

    LOGGER.debug("Deleting avatars for {} students from faculty with ID: {}", students.size(), facultyId);
    for (Student student : students) {
      avatarRepository.deleteByStudentId(student.getId());
    }

    LOGGER.debug("Deleting all students from faculty with ID: {}", facultyId);
    studentRepository.deleteAllByFaculty_Id(facultyId);
  }

  public long getCountByAllStudens() {
    LOGGER.info("Was invoked method for get count of all students");
    long count = studentRepository.getCountByAllStudens();
    LOGGER.debug("Total students count: {}", count);
    return count;
  }

  public Double findAverageAge() {
    LOGGER.info("Was invoked method for find average age of students");
    Double averageAge = studentRepository.findAverageAge();
    LOGGER.debug("Average age of students: {}", averageAge);
    return averageAge;
  }

  public List<Student> findTop5ByOrderByIdDesc() {
    LOGGER.info("Was invoked method for find top 5 students ordered by ID desc");
    List<Student> students = studentRepository.findTop5ByOrderByIdDesc();
    LOGGER.debug("Found {} latest students", students.size());
    return students;
  }
}