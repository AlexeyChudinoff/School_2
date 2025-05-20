package ru.hogwarts.school_2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "students")
//@JsonIgnoreProperties({"faculty"}) // Исключаем поле faculty из сериализации
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(hidden = true)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer age;

  @Column(nullable = false, length = 1)
  private String gender;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id")//через что связаны студенты и факультеты мы активная сторона
  @JsonIgnore // Пропускаем данное поле при сериализации
  private Faculty faculty;


  //mappedBy - это поле в другой стороне, мы пассивная сторона
  //Используя cascade = CascadeType.REMOVE, мы гарантируем, что
  // при удалении студента соответствующий аватар будет удалён автоматически.
  @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Avatar avatar;


  public Student() {
  }

  public Student(String name, Integer age, String gender) {
    this.name = name;
    this.age = age;
    this.gender = gender.toUpperCase().substring(0, 1);//делаем нечувствительным к регистру

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Student student = (Student) o;
    return Objects.equals(id, student.id) &&
        Objects.equals(name, student.name) &&
        Objects.equals(age, student.age) &&
        Objects.equals(gender, student.gender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, age, gender);
  }

}//class