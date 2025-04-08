package ru.hogwarts.School_2.model;

import jakarta.persistence.Id;
import java.util.Objects;

public class Student {

  @Id
  private long id;
  private String name;
  private int age;
  private String gender;

  public Student() {
  }

  public Student(String name, int age, String gender) {
    this.id = id;
    this.name = name;
    this.age = age;
    if (gender.equals("m") || gender.equals("f")) {
      this.gender = gender;
    } else {
      throw new IllegalArgumentException("Gender must be m - male or f - female");
    }
  }



  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getGender() {
    return gender;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
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
    Student student = (Student) o;
    return id == student.id && age == student.age && Objects.equals(name, student.name)
        && Objects.equals(gender, student.gender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, age, gender);
  }

  @Override
  public String toString() {
    return "Student{" +
        " id=" + id +
        ", name='" + name +
        ", age=" + age +
        ", gender='" + gender +
        '}';

  }

}//class
