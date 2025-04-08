package ru.hogwarts.school_2.servise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_2.model.Student;

@Service
public class StudentService {

  Map<Long, Student> studentsMap = new HashMap<>();

  public void initializeStudentsMap() {
    studentsMap.put(1L, new Student("Harry", 17, "m"));
    studentsMap.put(2L, new Student("Ron", 17, "m"));
    studentsMap.put(3L, new Student("Hermione", 16, "f"));
  }

  public Student getStudentByName(String name) {
    for (Student student : studentsMap.values()) {
      if (student.getName().equals(name)) {
        return student;
      }
    }
    return null;
  }

  public Student getStudentByAge(int age) {
    for (Student student : studentsMap.values()) {
      if (student.getAge() == age) {
        return student;
      }
    }
    return null;
  }

  long idCounter = 0;

  // С
  public void addStudent(Student student) {
    idCounter++;
    student.setId(idCounter);
    studentsMap.put(idCounter, student);
  }

  // R
  public Student getStudent(long id) {
    return studentsMap.get(id);
  }

  public List<Student> getAllStudents() {
    if (studentsMap.isEmpty()) {
      return null;
    } else {
      return new ArrayList<>(studentsMap.values());
    }
  }


  // U
  public Student updeteStudent(Student student) {
    return studentsMap.replace(student.getId(), student);
  }

  // D
  public void deleteStudent(long id) {
    studentsMap.remove(id);
  }


}//
