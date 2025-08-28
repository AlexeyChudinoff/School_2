package ru.hogwarts.school_2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/test")
  public String test() {
    return "Application is running!";
  }

  @GetMapping("/test-db")
  public String testDb() {
    try {
      // Простая проверка соединения с БД
      return "DB connection OK!";
    } catch (Exception e) {
      return "DB connection FAILED: " + e.getMessage();
    }
  }
}