package ru.hogwarts.school_2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

  @Value("${server.port}") // Получаем значение порта из application.properties
  private int serverPort;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @GetMapping("/port")
  public int getPort() {
    return serverPort;
  }

  @GetMapping("/db-info")
  public String getDbInfo() {
    return "DB URL: " + dbUrl;
  }


}//class