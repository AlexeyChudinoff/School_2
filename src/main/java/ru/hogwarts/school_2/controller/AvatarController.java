package ru.hogwarts.school_2.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school_2.exception.AvatarProcessingException;
import ru.hogwarts.school_2.model.Avatar;
import ru.hogwarts.school_2.service.AvatarService;

@RestController
@RequestMapping("/avatars")
@Transactional
public class AvatarController {

  private final AvatarService avatarService;

  private static final Logger log = LoggerFactory.getLogger(AvatarController.class);

  public AvatarController(AvatarService avatarService) {
    this.avatarService = avatarService;
  }

  @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId,
      @RequestParam MultipartFile file) throws IOException {
    try {
      avatarService.uploadAvatar(studentId, file);
      return ResponseEntity.ok("Аватар успешно загружен");
    } catch (IOException | AvatarProcessingException e) {
      log.error("Ошибка при загрузке аватара {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @GetMapping("/{studentId}/from-db")
  public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
    Avatar avatar = avatarService.findAvatar(studentId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
    headers.setContentLength(avatar.getData().length);

    return ResponseEntity.ok()
        .headers(headers)
        .body(avatar.getData());
  }

  @GetMapping("/{studentId}/from-file")
  public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) {
    byte[] data = avatarService.getAvatarFileData(studentId);
    Avatar avatar = avatarService.findAvatar(studentId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
    headers.setContentLength(data.length);

    return ResponseEntity.ok()
        .headers(headers)
        .body(data);
  }

  @Operation(summary = "Получить список аватаров по 4 на странице")
  @GetMapping("/page/{pageNumber}")
  public ResponseEntity<Iterable<Avatar>> getAvatarsByPage(
      @PathVariable int pageNumber) {

    if (pageNumber <= 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // возвращаем ошибку 400, если страница некорректная
    }

    // получение нужного набора аватаров из сервиса
    Iterable<Avatar> avatars = avatarService.getAvatarsByPage04(pageNumber);

    return new ResponseEntity<>(avatars, HttpStatus.OK); // 200 успешный возврат результата
  }



}//