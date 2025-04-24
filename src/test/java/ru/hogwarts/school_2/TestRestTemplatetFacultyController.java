//TestRestTemplatetFacultyController
package ru.hogwarts.school_2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.FacultyRepository;


import java.net.URI;
import ru.hogwarts.school_2.repository.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @Autowired
  StudentRepository studentRepository;

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
  public void getFacultyByColorTest() throws UnsupportedEncodingException {
    // Создаем временный факультет
    Faculty faculty = new Faculty("Биология", "Бирюзовый");
    facultyRepository.save(faculty);

    // Кодируем цвет для передачи в URL
    String encodedColor = URLEncoder.encode("Бирюзовый", "UTF-8");

    // Формируем URL с закодированным цветом
    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyByColor?color=" + encodedColor);

    // Отправляем запрос и принимаем ответ в виде строки
    ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

    // Проверяем статус ответа
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Распечатываем тело ответа для анализа
    System.out.println("Тело ответа: " + response.getBody());

    // Если надо проверить, что ответ содержит нужную информацию
    assertTrue(response.getBody().contains("Бирюзовый")); // или другое условие проверки
  }

  /**
   * Тест на получение факультета по имени.
   */
  @Test
  public void getFacultyByNameTest() throws UnsupportedEncodingException {
    // Создаем временный факультет
    Faculty faculty = new Faculty("Биология", "Бирюзовый");
    facultyRepository.save(faculty);

    // Кодируем название факультета для передачи в URL
    String encodedName = URLEncoder.encode("Биология", "UTF-8");

    // Формируем URL с закодированным названием
    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyByName?name=" + encodedName);

    // Отправляем запрос и принимаем ответ в виде строки
    ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

    // Проверяем статус ответа
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Распечатываем тело ответа для анализа
    System.out.println("Тело ответа: " + response.getBody());

    // Если надо проверить, что ответ содержит нужную информацию
    assertTrue(response.getBody().contains("Биология")); // или другое условие проверки
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
   * Тест на поиск факультетов по фрагментам имени или цвета.
   */
  @Test
  public void searchByNameOrColorTest() {
    // Создаем факультеты с подходящими значениями
    Faculty faculty1 = new Faculty("Информационные технологии", "Синий");
    Faculty faculty2 = new Faculty("Менеджмент", "Желтый");
    facultyRepository.save(faculty1);
    facultyRepository.save(faculty2);

    // Формируем URL с русским текстом, закодированным для передачи
    String encodedName = URLEncoder.encode("инф", StandardCharsets.UTF_8);
    String encodedColor = URLEncoder.encode("желт", StandardCharsets.UTF_8);
    URI uri = URI.create("http://localhost:" + port + "/faculty/searchByNameOrColor?name=" + encodedName + "&color=" + encodedColor);

    // Получаем ответ в правильном формате (массив факультетов)
    ResponseEntity<Faculty[]> response = restTemplate.getForEntity(uri, Faculty[].class);

    // Проверка статуса ответа
    if (response.getStatusCode() == HttpStatus.NO_CONTENT) { // Если ничего не найдено
      System.out.println("Запросов не найдено.");
    } else {
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // Должен быть статус 200 OK
      assertThat(response.getBody().length).isGreaterThan(0); // Количество должно быть больше нуля
    }
  }

  /**
   * Тест на получение факультета по ID студента.
   */
  @Test
  public void getFacultyByStudentIdTest() {
    // Создаем временный факультет
    Faculty faculty = new Faculty("Программирование", "Синий");
    facultyRepository.save(faculty);

    // Создаем студента, привязанного к данному факультету
    Student student = new Student("Иван Иванов", 20, "м");
    student.setFaculty(faculty); // Связываем студента с факультетом
    studentRepository.save(student); // Сохраняем студента в БД

    // ЧИТАЕМ НАЗНАЧЕННЫЙ ИДЕНТИКАТОР СТУДЕНТА ПОСЛЕ СОХРАНЕНИЯ
    long studentId = student.getId();

    // Формируем URL с ID студента
    URI uri = URI.create("http://localhost:" + port + "/faculty/getFacultyByStudentId?id=" + studentId);

    // Отправляем запрос и обрабатываем ответ
    ResponseEntity<Faculty> response = restTemplate.getForEntity(uri, Faculty.class);

    // Проверяем статус ответа
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Проверяем полученные данные
    Faculty returnedFaculty = response.getBody();
    assertEquals("Программирование", returnedFaculty.getName());
    assertEquals("Синий", returnedFaculty.getColor());
  }

}