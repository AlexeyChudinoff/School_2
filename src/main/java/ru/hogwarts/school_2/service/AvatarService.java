package ru.hogwarts.school_2.service;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school_2.exception.AvatarProcessingException;
import ru.hogwarts.school_2.exception.StudentNotFoundException;
import ru.hogwarts.school_2.model.Avatar;
import ru.hogwarts.school_2.model.Student;
import ru.hogwarts.school_2.repository.AvatarRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

  @Value("${avatars.storage.path}")
  private String avatarsStoragePath;

  private final StudentService studentService;
  private final AvatarRepository avatarRepository;

  public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
    this.studentService = studentService;
    this.avatarRepository = avatarRepository;
  }

  @Transactional
  public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
    // Проверяем существование студента по его ID
    Optional<Student> studentOpt = studentService.getStudentById(studentId);
    if (studentOpt.isEmpty()) {
      throw new StudentNotFoundException("Студент с указанным ID не найден: " + studentId);
    }

    Student student = studentOpt.get();

    if (file.isEmpty()) {
      throw new AvatarProcessingException("Файл аватара не может быть пустым");
    }

    Path filePath = Path.of(avatarsStoragePath, studentId + "." + getExtension(file.getOriginalFilename()));
    Files.createDirectories(filePath.getParent());
    Files.deleteIfExists(filePath);

    try (InputStream is = file.getInputStream();
        OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
        BufferedInputStream bis = new BufferedInputStream(is, 1024);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
      bis.transferTo(bos);
    } catch (IOException e) {
      throw new AvatarProcessingException("Ошибка при сохранении файла на диск", e);
    }

    Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    avatar.setStudent(student);
    avatar.setFilePath(filePath.toString());
    avatar.setFileSize(file.getSize());
    avatar.setMediaType(file.getContentType());

    try {
      avatar.setData(file.getBytes());
    } catch (IOException e) {
      throw new AvatarProcessingException("Ошибка при чтении файла", e);
    }

    avatarRepository.save(avatar);
  }

  private String getExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

  public Avatar findAvatar(Long studentId) {
    return avatarRepository.findByStudentId(studentId)
        .orElseThrow(() -> new AvatarProcessingException("Аватар для студента с ID " + studentId + " не найден"));
  }

  public byte[] getAvatarFileData(Long studentId) {
    Avatar avatar = findAvatar(studentId);
    Path filePath = Path.of(avatar.getFilePath());

    try {
      return Files.readAllBytes(filePath);
    } catch (IOException e) {
      throw new AvatarProcessingException("Ошибка при чтении файла аватара с диска", e);
    }
  }


  @Operation(summary = "Получить аватары по 4 на странице")
  public Iterable<Avatar> getAvatarsByPage04(int pageNumber) {
          return avatarRepository.findAllByPage04(pageNumber -1);

  }

}//class