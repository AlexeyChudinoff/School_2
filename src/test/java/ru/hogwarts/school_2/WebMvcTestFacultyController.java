package ru.hogwarts.school_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school_2.controller.FacultyController;
import ru.hogwarts.school_2.dto.FacultyDTO;
import ru.hogwarts.school_2.model.Faculty;
import ru.hogwarts.school_2.service.FacultyService;

@ExtendWith(MockitoExtension.class)
class WebMvcTestFacultyController {

  @Mock
  private FacultyService facultyService;

  @InjectMocks
  private FacultyController facultyController;

  @Test
  void createFaculty_возвращаетСозданныйФакультет() {
    Faculty faculty = new Faculty();
    faculty.setName("Гриффиндор");
    faculty.setColor("Красный");

    when(facultyService.addFaculty(faculty)).thenReturn(faculty);

    ResponseEntity<Faculty> response = facultyController.createFaculty(faculty);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(faculty, response.getBody());
    verify(facultyService, times(1)).addFaculty(faculty);
  }

  @Test
  void updateFaculty_возвращаетОбновленныйФакультет() {
    Long id = 1L;
    Faculty faculty = new Faculty();
    faculty.setId(id);
    faculty.setName("Гриффиндор");
    faculty.setColor("Красный");

    when(facultyService.updateFaculty(faculty)).thenReturn(faculty);

    ResponseEntity<Faculty> response = facultyController.updateFaculty(id, faculty);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(faculty, response.getBody());
    verify(facultyService, times(1)).updateFaculty(faculty);
  }

  @Test
  void getFacultyByColor_возвращаетФакультетЕслиНайден() {
    String color = "Красный";
    Faculty faculty = new Faculty();
    faculty.setColor(color);

    when(facultyService.getFacultyByColor(color)).thenReturn(Optional.of(faculty));

    ResponseEntity<Optional<Faculty>> response = facultyController.getFacultyByColor(color);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isPresent());
    assertEquals(color, response.getBody().get().getColor());
    verify(facultyService, times(1)).getFacultyByColor(color);
  }

  @Test
  void getFacultyByColor_возвращаетNotFoundЕслиНеНайден() {
    String color = "Неизвестный";

    when(facultyService.getFacultyByColor(color)).thenReturn(Optional.empty());

    ResponseEntity<Optional<Faculty>> response = facultyController.getFacultyByColor(color);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(facultyService, times(1)).getFacultyByColor(color);
  }

  @Test
  void getFacultyByName_возвращаетФакультетЕслиНайден() {
    String name = "Гриффиндор";
    Faculty faculty = new Faculty();
    faculty.setName(name);

    when(facultyService.getFacultyByName(name)).thenReturn(Optional.of(faculty));

    ResponseEntity<Optional<Faculty>> response = facultyController.getFacultyByName(name);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isPresent());
    assertEquals(name, response.getBody().get().getName());
    verify(facultyService, times(1)).getFacultyByName(name);
  }

  @Test
  void getFacultyByName_возвращаетNotFoundЕслиНеНайден() {
    String name = "Неизвестный";

    when(facultyService.getFacultyByName(name)).thenReturn(Optional.empty());

    ResponseEntity<Optional<Faculty>> response = facultyController.getFacultyByName(name);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(facultyService, times(1)).getFacultyByName(name);
  }

  @Test
  void getFacultyById_возвращаетФакультетЕслиНайден() {
    Long id = 1L;
    Faculty faculty = new Faculty();
    faculty.setId(id);

    when(facultyService.getFacultyById(id)).thenReturn(Optional.of(faculty));

    ResponseEntity<Optional<Faculty>> response = facultyController.getFacultyById(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isPresent());
    assertEquals(id, response.getBody().get().getId());
    verify(facultyService, times(1)).getFacultyById(id);
  }

  @Test
  void getFacultyById_возвращаетNotFoundЕслиНеНайден() {
    Long id = 999L;

    when(facultyService.getFacultyById(id)).thenReturn(Optional.empty());

    ResponseEntity<Optional<Faculty>> response = facultyController.getFacultyById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(facultyService, times(1)).getFacultyById(id);
  }

  @Test
  void searchFaculties_возвращаетФакультетыЕслиНайдены() {
    String name = "Гриффиндор";
    String color = "Красный";
    Faculty faculty = new Faculty();
    faculty.setName(name);
    faculty.setColor(color);

    when(facultyService.findByNameOrColor(name, color)).thenReturn(List.of(faculty));

    ResponseEntity<List<Faculty>> response = facultyController.searchFaculties(name, color);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals(name, response.getBody().get(0).getName());
    verify(facultyService, times(1)).findByNameOrColor(name, color);
  }

  @Test
  void searchFaculties_возвращаетNoContentЕслиНеНайдены() {
    String name = "Неизвестный";
    String color = "Неизвестный";

    when(facultyService.findByNameOrColor(name, color)).thenReturn(List.of());

    ResponseEntity<List<Faculty>> response = facultyController.searchFaculties(name, color);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(facultyService, times(1)).findByNameOrColor(name, color);
  }

  @Test
  void getFacultyByStudentId_возвращаетФакультетЕслиНайден() {
    Long studentId = 1L;
    FacultyDTO facultyDTO = new FacultyDTO(1L, "Гриффиндор", "Красный");

    when(facultyService.getFacultyByStudentId(studentId)).thenReturn(facultyDTO);

    ResponseEntity<FacultyDTO> response = facultyController.getFacultyByStudentId(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(facultyDTO, response.getBody());
    verify(facultyService, times(1)).getFacultyByStudentId(studentId);
  }

  @Test
  void getFacultyByStudentId_возвращаетNotFoundЕслиСтудентНеНайден() {
    Long studentId = 999L;

    when(facultyService.getFacultyByStudentId(studentId))
        .thenThrow(new EntityNotFoundException("Студент не найден"));

    ResponseEntity<FacultyDTO> response = facultyController.getFacultyByStudentId(studentId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(facultyService, times(1)).getFacultyByStudentId(studentId);
  }

  @Test
  void getAllFaculties_возвращаетВсеФакультеты() {
    Faculty faculty1 = new Faculty();
    faculty1.setName("Гриффиндор");
    Faculty faculty2 = new Faculty();
    faculty2.setName("Слизерин");

    when(facultyService.getAllFaculties()).thenReturn(List.of(faculty1, faculty2));

    ResponseEntity<List<Faculty>> response = facultyController.getAllFaculties();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(facultyService, times(1)).getAllFaculties();
  }

  @Test
  void getAllFaculties_возвращаетNoContentЕслиНетФакультетов() {
    when(facultyService.getAllFaculties()).thenReturn(List.of());

    ResponseEntity<List<Faculty>> response = facultyController.getAllFaculties();

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(facultyService, times(1)).getAllFaculties();
  }

  @Test
  void deleteFacultyById_возвращаетOkПриУдалении() {
    Long id = 1L;
    Faculty faculty = new Faculty();
    faculty.setId(id);

    when(facultyService.getFacultyById(id)).thenReturn(Optional.of(faculty));

    ResponseEntity<String> response = facultyController.deleteFacultyById(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Факультет успешно удалён", response.getBody());
    verify(facultyService, times(1)).getFacultyById(id);
    verify(facultyService, times(1)).deleteFacultyById(id);
  }

  @Test
  void deleteFacultyById_возвращаетNotFoundЕслиФакультетНеНайден() {
    Long id = 999L;

    when(facultyService.getFacultyById(id)).thenReturn(Optional.empty());

    ResponseEntity<String> response = facultyController.deleteFacultyById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(facultyService, times(1)).getFacultyById(id);
    verify(facultyService, never()).deleteFacultyById(id);
  }
}//