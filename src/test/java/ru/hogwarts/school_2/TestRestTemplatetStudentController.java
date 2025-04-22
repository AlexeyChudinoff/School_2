package ru.hogwarts.school_2;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school_2.controller.StudentController;
import ru.hogwarts.school_2.repository.AvatarRepository;
import ru.hogwarts.school_2.repository.FacultyRepository;
import ru.hogwarts.school_2.repository.StudentRepository;
import ru.hogwarts.school_2.service.AvatarService;
import ru.hogwarts.school_2.service.FacultyService;
import ru.hogwarts.school_2.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTestRestTemplateTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private StudentRepository studentRepository;
  @MockBean
  private FacultyRepository facultyRepository;
  @MockBean
  private AvatarRepository avatarRepository;
  @SpyBean
  private StudentService studentService;
  @SpyBean
  private FacultyService facultyService;
  @SpyBean
  private AvatarService avatarService;
  @InjectMocks
  private StudentController studentController;



}