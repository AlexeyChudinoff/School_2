//TestRestTemplatetFacultyController
package ru.hogwarts.school_2;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.repository.FacultyRepository;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestRestTemplatetFacultyController {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private FacultyRepository facultyRepository;

  @BeforeEach
  public void clearDb() {
    facultyRepository.deleteAll();
  }

  /**
   * Тест на создание факультета.
   */
  @Test
  public void createFacultyTest() {
    URI uri = URI.create("http://localhost:" + port + "/faculty/addFaculty");
    Faculty faculty = new Faculty("Медицина", "Зелёный");

    ResponseEntity<Faculty> response = restTemplate.postForEntity(uri, faculty, Faculty.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().getName()).isEqualTo("Медицина");
  }

  /**
   * Тест на обновление факультета.
   */
  @Test
  public void updateFacultyTest() {
    // Создаем факультет
    Faculty faculty = new Faculty("Физкультура", "Красный");
    facultyRepository.save(faculty);

    // Обновляем данные
    faculty.setName("Философия");
    faculty.setColor("Голубой");

    URI uri = URI.create("http://localhost:" + port + "/faculty/" + faculty.getId());
    restTemplate.put(uri, faculty); // фикс: метод put не принимает второй аргумент

    // Проверяем изменения
    Faculty updatedFaculty = facultyRepository.findById(faculty.getId()).get();
    assertThat(updatedFaculty.getName()).isEqualTo("Философия");
    assertThat(updatedFaculty.getColor()).isEqualTo("Голубой");
  }

  /**
   * Тест на получение факультета по ID.
   */
  @Test
  public void getFacultyByIdTest() {
    // Создаем факультет
    Faculty faculty = new Faculty("Математика", "Белый");
    facultyRepository.save(faculty);

    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyById?id=" + faculty.getId());
    ResponseEntity<Faculty> response = restTemplate.getForEntity(uri, Faculty.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Математика");
  }

  /**
   * Тест на получение факультета по цвету.
   */
  @Test
  public void getFacultyByColorTest() {
    // Создаем факультет
    Faculty faculty = new Faculty("Химия", "Серый");
    facultyRepository.save(faculty);

    // Формируем простой URL без экранирования
    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyByColor?color=Серый");

    // Получаем ответ в правильном формате (один объект Faculty)
    ResponseEntity<Faculty> response = restTemplate.getForEntity(uri, Faculty.class);

    // Проверяем статус ответа
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // Проверяем, что получили правильный факультет
    assertThat(response.getBody().getName()).isEqualTo("Химия");
  }

  /**
   * Тест на получение факультета по имени.
   */
  @Test
  public void getFacultyByNameTest() {
    // Создаем факультет
    Faculty faculty = new Faculty("Биология", "Бирюзовый");
    facultyRepository.save(faculty);

    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyByName?name=Биология");
    ResponseEntity<Faculty> response = restTemplate.getForEntity(uri, Faculty.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Биология");
  }

  /**
   * Тест на получение всех факультетов.
   */
  @Test
  public void getAllFacultiesTest() {
    // Создаем несколько факультетов
    facultyRepository.save(new Faculty("Физика", "Черный"));
    facultyRepository.save(new Faculty("Литература", "Золотой"));

    URI uri = URI.create("http://localhost:" + port + "/faculty/getAllFaculties");
    ResponseEntity<Faculty[]> response = restTemplate.getForEntity(uri, Faculty[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().length).isGreaterThan(0);
  }

  /**
   * Тест на удаление факультета.
   */
  @Test
  public void deleteFacultyTest() {
    // Создаем факультет
    Faculty faculty = new Faculty("Экология", "Зелёный");
    facultyRepository.save(faculty);

    URI uri = URI.create("http://localhost:" + port + "/faculty/deleteFacultyById?id=" + faculty.getId());
    restTemplate.delete(uri); // фикс: метод delete не принимает второй аргумент

    // Проверяем, что факультет удалился
    assertThat(facultyRepository.findById(faculty.getId())).isEmpty();
  }

  /**
   * Тест на поиск факультетов по имени или цвету.
   */
  @Test
  public void searchByNameOrColorTest() {
    // Создаем несколько факультетов
    facultyRepository.save(new Faculty("Генетика", "Розовый"));
    facultyRepository.save(new Faculty("Астрономия", "Синий"));

    // Формируем URL с русским текстом
    URI uri = URI.create("http://localhost:" + port + "/faculty/searchByNameOrColor?name=Астр&color=Роз");

    // Получаем ответ в правильном формате (массив факультетов)
    ResponseEntity<Faculty[]> response = restTemplate.getForEntity(uri, Faculty[].class);

    // Проверяем статус ответа
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // Проверяем, что получили хотя бы один факультет
    assertThat(response.getBody().length).isGreaterThan(0);

    // Дополнительно проверяем, что нашли нужное количество факультетов
    assertThat(response.getBody().length).isEqualTo(2); // Ожидается, что найдутся оба факультета
  }

  /**
   * Тест на получение факультета по ID студента.
   */
  @Test
  public void getFacultyByStudentIdTest() {
    // Пусть у нас есть студент с ID 1, принадлежащий определенному факультету
    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyByStudentId?id=1");
    ResponseEntity<Faculty> response = restTemplate.getForEntity(uri, Faculty.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isNotNull();
  }

}