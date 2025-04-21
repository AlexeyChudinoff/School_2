package ru.hogwarts.school_2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hogwarts.school_2.model.Faculty;

//аннотации Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor),
// автоматически генерируют:
//
//Конструктор без аргументов
//
//Конструктор со всеми аргументами
//
//Геттеры/сеттеры
//
//toString(), equals(), hashCode()

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для факультета")
public class FacultyDTO {
  @Schema(description = "ID факультета", example = "1")
  private Long id;

  @Schema(description = "Название факультета", example = "Гриффиндор")
  private String name;

  @Schema(description = "Цвет факультета", example = "Красный")
  private String color;

  // Фабричный метод для преобразования из Entity
  public static FacultyDTO fromFaculty(Faculty faculty) {
    if (faculty == null) {
      return null;
    }
    return new FacultyDTO(
        faculty.getId(),
        faculty.getName(),
        faculty.getColor()
    );
  }



}//

