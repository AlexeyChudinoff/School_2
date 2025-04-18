package ru.hogwarts.school_2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

}//