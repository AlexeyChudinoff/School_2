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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

  private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

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
    logger.info("Was invoked method for upload avatar for student ID: {}", studentId);

    logger.debug("Checking if student with ID {} exists", studentId);
    Optional<Student> studentOpt = studentService.getStudentById(studentId);
    if (studentOpt.isEmpty()) {
      logger.error("Student with ID {} not found", studentId);
      throw new StudentNotFoundException("Студент с указанным ID не найден: " + studentId);
    }

    Student student = studentOpt.get();
    logger.debug("Student found: ID={}, name={}", student.getId(), student.getName());

    if (file.isEmpty()) {
      logger.error("Uploaded file is empty for student ID: {}", studentId);
      throw new AvatarProcessingException("Файл аватара не может быть пустым");
    }

    String extension = getExtension(file.getOriginalFilename());
    Path filePath = Path.of(avatarsStoragePath, studentId + "." + extension);
    logger.debug("Preparing to save avatar to: {}", filePath);

    Files.createDirectories(filePath.getParent());
    Files.deleteIfExists(filePath);

    try (InputStream is = file.getInputStream();
        OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
        BufferedInputStream bis = new BufferedInputStream(is, 1024);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
      logger.debug("Saving avatar file to disk");
      bis.transferTo(bos);
    } catch (IOException e) {
      logger.error("Error saving avatar file to disk for student ID: {}", studentId, e);
      throw new AvatarProcessingException("Ошибка при сохранении файла на диск", e);
    }

    Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    logger.debug("{} avatar for student ID: {}",
        avatar.getId() == null ? "Creating new" : "Updating existing", studentId);

    avatar.setStudent(student);
    avatar.setFilePath(filePath.toString());
    avatar.setFileSize(file.getSize());
    avatar.setMediaType(file.getContentType());

    try {
      logger.debug("Reading avatar file bytes");
      avatar.setData(file.getBytes());
    } catch (IOException e) {
      logger.error("Error reading avatar file bytes for student ID: {}", studentId, e);
      throw new AvatarProcessingException("Ошибка при чтении файла", e);
    }

    Avatar savedAvatar = avatarRepository.save(avatar);
    logger.info("Avatar saved successfully for student ID: {}. Avatar ID: {}",
        studentId, savedAvatar.getId());
  }

  private String getExtension(String fileName) {
    logger.debug("Getting file extension for: {}", fileName);
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

  public Avatar findAvatar(Long studentId) {
    logger.info("Was invoked method for find avatar by student ID: {}", studentId);

    return avatarRepository.findByStudentId(studentId)
        .orElseThrow(() -> {
          logger.error("Avatar not found for student ID: {}", studentId);
          return new AvatarProcessingException("Аватар для студента с ID " + studentId + " не найден");
        });
  }

  public byte[] getAvatarFileData(Long studentId) {
    logger.info("Was invoked method for get avatar file data by student ID: {}", studentId);

    Avatar avatar = findAvatar(studentId);
    Path filePath = Path.of(avatar.getFilePath());
    logger.debug("Reading avatar file from: {}", filePath);

    try {
      byte[] data = Files.readAllBytes(filePath);
      logger.debug("Successfully read avatar file data for student ID: {}", studentId);
      return data;
    } catch (IOException e) {
      logger.error("Error reading avatar file from disk for student ID: {}", studentId, e);
      throw new AvatarProcessingException("Ошибка при чтении файла аватара с диска", e);
    }
  }

  @Operation(summary = "Получить аватары по 4 на странице")
  public Iterable<Avatar> getAvatarsByPage04(int pageNumber) {
    logger.info("Was invoked method for get avatars by page (4 per page). Page: {}", pageNumber);

    Iterable<Avatar> avatars = avatarRepository.findAllByPage04(pageNumber - 1);
    logger.debug("Retrieved avatars for page {}", pageNumber);
    return avatars;
  }
}//class