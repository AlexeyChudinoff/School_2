package ru.hogwarts.school_2.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Faculty {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Название не может быть пустым")
  @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
  private String name;

  @NotBlank(message = "Цвет не может быть пустым")
  @Size(min = 2, max = 30, message = "Цвет должен быть от 2 до 30 символов")
  private String color;

  @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Student> students = new ArrayList<>();  // Должно быть множественным числом и коллекцией


  public Faculty() {
  }

  public Faculty(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Faculty faculty = (Faculty) o;
    return Objects.equals(id, faculty.id) && Objects.equals(name, faculty.name)
        && Objects.equals(color, faculty.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, color);
  }

  @Override
  public String toString() {
    return "Faculty{" + ", id=" + id +
        ", name='" + name + "color='" + color + '}';
  }


}//class
