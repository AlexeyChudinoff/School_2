package ru.hogwarts.school_2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Avatar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String filePath;

  @Column(nullable = false)
  private Long fileSize;

  @Column(nullable = false)
  private String mediaType;

  @Lob
  private byte[] data;

  @OneToOne
  private Student student;


  public Avatar() {
  }

  public Avatar(String url, Long fileSize) {
    this.filePath = url;
    this.fileSize = fileSize;
  }


}//
