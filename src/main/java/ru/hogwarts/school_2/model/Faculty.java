package ru.hogwarts.school_2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString(exclude = "students")
public class Faculty {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Название не может быть пустым")
  @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
  @Column(nullable = false, length = 50, unique = true)
  private String name;

  @NotBlank(message = "Цвет не может быть пустым")
  @Size(min = 2, max = 30, message = "Цвет должен быть от 2 до 30 символов")
  @Column(nullable = false, length = 30)
  private String color;

  @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JsonIgnore
  private List<Student> students = new ArrayList<>();

  public Faculty() {//хибернет умеет работать только через пустой конструктор
  }
  public Faculty(String name, String color) {
    this.name = name;
    this.color = color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Faculty faculty = (Faculty) o;
    return Objects.equals(id, faculty.id) &&
        Objects.equals(name, faculty.name) &&
        Objects.equals(color, faculty.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, color);
  }

}//