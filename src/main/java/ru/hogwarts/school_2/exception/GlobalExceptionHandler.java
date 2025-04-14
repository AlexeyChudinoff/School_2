package ru.hogwarts.school_2.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
    logger.warn("Сущность не найдена: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
    logger.warn("Некорректное состояние: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    logger.warn("Некорректный аргумент: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    logger.warn("Ошибка валидации: {}", errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAllExceptions(Exception ex) {
    logger.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Произошла внутренняя ошибка сервера");
  }
}


//package ru.hogwarts.school_2.exception;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//  // Обработка когда сущность не найдена
//  @ExceptionHandler(EntityNotFoundException.class)
//  public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
//    logger.warn("Сущность не найдена: {}", ex.getMessage());
//    return ResponseEntity
//        .status(HttpStatus.NOT_FOUND)
//        .body(ex.getMessage());
//  }
//
//  // Обработка некорректного состояния
//  @ExceptionHandler(IllegalStateException.class)
//  public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
//    logger.warn("Некорректное состояние: {}", ex.getMessage());
//    return ResponseEntity
//        .status(HttpStatus.BAD_REQUEST)
//        .body(ex.getMessage());
//  }
//
//  // Обработка всех остальных исключений
//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<String> handleAllExceptions(Exception ex) {
//    logger.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
//    return ResponseEntity
//        .status(HttpStatus.INTERNAL_SERVER_ERROR)
//        .body("Произошла внутренняя ошибка сервера");
//  }
//
//  // EntityNotFoundException.java
//  public class EntityNotFoundException extends RuntimeException {
//    public EntityNotFoundException(String message) {
//      super(message);
//    }
//  }
//
//  // FacultyNotFoundException.java
//  public class FacultyNotFoundException extends EntityNotFoundException {
//    public FacultyNotFoundException(Long id) {
//      super("Факультет с ID " + id + " не найден");
//    }
//  }
//
//  // StudentNotFoundException.java
//  public class StudentNotFoundException extends EntityNotFoundException {
//    public StudentNotFoundException(Long id) {
//      super("Студент с ID " + id + " не найден");
//    }
//  }
//
//}