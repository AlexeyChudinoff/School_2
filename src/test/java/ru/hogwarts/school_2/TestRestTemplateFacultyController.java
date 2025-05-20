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
    faculty.setName("Тестовый факультет");
    faculty.setColor("Красный");

    ResponseEntity<Faculty> response = restTemplate.postForEntity(
        getBaseUrl() + "/addFaculty",
        faculty,
        Faculty.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Тестовый факультет", response.getBody().getName());
  }

  @Test
  void getFacultyById_shouldReturnFacultyWhenExists() {
    // Сначала создаем факультет
    Faculty faculty = new Faculty();
    faculty.setName("Гриффиндор");
    faculty.setColor("Алый");
    Faculty createdFaculty = restTemplate.postForObject(
        getBaseUrl() + "/addFaculty", faculty, Faculty.class);

    // Затем получаем его по ID
    ResponseEntity<Faculty> response = restTemplate.getForEntity(
        getBaseUrl() + "/getFacultyById?id=" + createdFaculty.getId(),
        Faculty.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Гриффиндор", response.getBody().getName());
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
    faculty1.setName("Когтевран");
    faculty1.setColor("Синий");
    restTemplate.postForEntity(getBaseUrl() + "/addFaculty", faculty1, Faculty.class);

    Faculty faculty2 = new Faculty();
    faculty2.setName("Пуффендуй");
    faculty2.setColor("Желтый");
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
    faculty.setName("Слизерин");
    faculty.setColor("Зеленый");
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
