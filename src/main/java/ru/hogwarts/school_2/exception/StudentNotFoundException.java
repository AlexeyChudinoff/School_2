package ru.hogwarts.school_2.exception;

import jakarta.persistence.EntityNotFoundException;

public class StudentNotFoundException extends EntityNotFoundException {
  public StudentNotFoundException(String id) {
    super("Студент с ID " + id + " не найден");
  }
}