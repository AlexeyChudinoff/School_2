package ru.hogwarts.school_2.dto;


import ru.hogwarts.school_2.model.Student;

public class StudentDTO {

  private Long id;
  private String name;
  private int age;
  private String gender;
  private Long facultyId; // только ID факультета

//  public StudentDTO(Long id, String name, int age, String gender, Long facultyId) {
//    this.age = age;
//    this.facultyId = facultyId;
//    this.id = id;
//    this.name = name;
//    if (gender.equals("м") || gender.equals("ж")) {
//      this.gender = gender;
//    } else {
//      throw new IllegalArgumentException("Gender must be м - male or ж - female");
//    }
//  }

  public static StudentDTO StudentDtoFromStudent(Student student) {
    if (student == null) {
      return null;
    }
    StudentDTO studentDTO = new StudentDTO();
    studentDTO.setId(student.getId());
    studentDTO.setName(student.getName());
    studentDTO.setAge(student.getAge());
    studentDTO.setGender(student.getGender());
    studentDTO.setFacultyId(student.getFaculty().getId());
 return studentDTO;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Long getFacultyId() {
    return facultyId;
  }

  public void setFacultyId(Long facultyId) {
    this.facultyId = facultyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}


