package ru.hogwarts.school_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school_2.model.Faculty;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class TestRestTemplateFacultyController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/faculty";
  }

  @Test
  void createFaculty_shouldReturnCreatedFaculty() {
    Faculty faculty = new Faculty();
    faculty.setName("Test Faculty");
    faculty.setColor("Red");

    ResponseEntity<Faculty> response = restTemplate.postForEntity(
        getBaseUrl() + "/addFaculty",
        faculty,
        Faculty.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Test Faculty", response.getBody().getName());
  }

  @Test
  void getFacultyById_shouldReturnFacultyWhenExists() {
    // Сначала создаем факультет
    Faculty faculty = new Faculty();
    faculty.setName("Gryffindor");
    faculty.setColor("Scarlet");
    Faculty createdFaculty = restTemplate.postForObject(
        getBaseUrl() + "/addFaculty", faculty, Faculty.class);

    // Затем получаем его по ID
    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/getFacultyById?id=" + createdFaculty.getId(),
        Faculty.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Gryffindor", response.getBody().getName());
  }

  @Test
  void getFacultyById_shouldReturnNotFoundWhenNotExists() {
    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/getFacultyById?id=9999",
        Faculty.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getAllFaculties_shouldReturnListOfFaculties() {
    // Создаем несколько факультетов
    Faculty faculty1 = new Faculty();
    faculty1.setName("Ravenclaw");
    faculty1.setColor("Blue");
    restTemplate.postForEntity(getBaseUrl() + "/addFaculty", faculty1, Faculty.class);

    Faculty faculty2 = new Faculty();
    faculty2.setName("Hufflepuff");
    faculty2.setColor("Yellow");
    restTemplate.postForEntity(getBaseUrl() + "/addFaculty", faculty2, Faculty.class);

    // Получаем все факультеты
    ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
        getBaseUrl() + "/getAllFaculties",
        Faculty[].class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().length >= 2);
  }

  @Test
  void deleteFacultyById_shouldDeleteFaculty() {
    // Создаем факультет для удаления
    Faculty faculty = new Faculty();
    faculty.setName("Slytherin");
    faculty.setColor("Green");
    Faculty createdFaculty = restTemplate.postForObject(
        getBaseUrl() + "/addFaculty", faculty, Faculty.class);

    // Удаляем факультет
    restTemplate.delete(getBaseUrl() + "/deleteFacultyById?id=" + createdFaculty.getId());

    // Проверяем, что факультет удален
    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/getFacultyById?id=" + createdFaculty.getId(),
        Faculty.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

}//