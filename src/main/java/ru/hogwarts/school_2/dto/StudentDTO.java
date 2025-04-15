package ru.hogwarts.school_2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для студента")
public class StudentDTO {
  @Schema(description = "ID студента", example = "1")
  private Long id;

  @NotBlank(message = "Имя не может быть пустым")
  @Schema(description = "Имя студента", example = "Гарри Поттер")
  private String name;

  @Min(value = 11, message = "Возраст должен быть не меньше 11")
  @Schema(description = "Возраст студента", example = "17")
  private int age;

  @Pattern(regexp = "[мж]", message = "Пол должен быть 'м' или 'ж'")
  @Schema(description = "Пол студента", example = "м")
  private String gender;

  @Schema(description = "ID факультета", example = "1")
  private Long facultyId;



  public static StudentDTO fromStudent(Student student) {
    if (student == null) {
      return null;
    }
    StudentDTO dto = new StudentDTO();
    dto.setId(student.getId());
    dto.setName(student.getName());
    dto.setAge(student.getAge());
    dto.setGender(student.getGender());
    if (student.getFaculty() != null) {
      dto.setFacultyId(student.getFaculty().getId());
    }
    return dto;
  }




}


//package ru.hogwarts.school_2.dto;
//
//
//import ru.hogwarts.school_2.model.Student;
//
//public class StudentDTO {
//
//  private Long id;
//  private String name;
//  private int age;
//  private String gender;
//  private Long facultyId; // только ID факультета
//
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
//
//  public StudentDTO() {
//  }
//
//  public static StudentDTO StudentDtoFromStudent(Student student) {
//    if (student == null) {
//      return null;
//    }
//    return new StudentDTO(
//        student.getId(),
//        student.getName(),
//        student.getAge(),
//        student.getGender(),
//        student.getFaculty() != null ? student.getFaculty().getId() : null
//    );
//  }
////    StudentDTO studentDTO = new StudentDTO();
////
////    studentDTO.setId(student.getId());
////    studentDTO.setName(student.getName());
////    studentDTO.setAge(student.getAge());
////    studentDTO.setGender(student.getGender());
////    studentDTO.setFacultyId(student.getFaculty().getId());
////    return studentDTO;
////  }
//
//  public Long getId() {
//    return id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public String getGender() {
//    return gender;
//  }
//
//  public void setGender(String gender) {
//    this.gender = gender;
//  }
//
//  public int getAge() {
//    return age;
//  }
//
//  public void setAge(int age) {
//    this.age = age;
//  }
//
//  public Long getFacultyId() {
//    return facultyId;
//  }
//
//  public void setFacultyId(Long facultyId) {
//    this.facultyId = facultyId;
//  }
//
//  public String getName() {
//    return name;
//  }
//
//  public void setName(String name) {
//    this.name = name;
//  }
//}
//
//
