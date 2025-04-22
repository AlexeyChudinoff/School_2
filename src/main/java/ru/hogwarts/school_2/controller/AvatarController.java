package ru.hogwarts.school_2.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school_2.exception.AvatarProcessingException;
import ru.hogwarts.school_2.model.Avatar;
import ru.hogwarts.school_2.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

  private final AvatarService avatarService;

  public AvatarController(AvatarService avatarService) {
    this.avatarService = avatarService;
  }

  @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId,
      @RequestParam MultipartFile file) throws IOException {
    try {
      avatarService.uploadAvatar(studentId, file);
      return ResponseEntity.ok("Аватар успешно загружен");
    } catch (IOException e) {
      throw new AvatarProcessingException("Ошибка при загрузке аватара", e);
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


}//