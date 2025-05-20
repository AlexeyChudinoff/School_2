package ru.hogwarts.school_2.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school_2.model.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

  Optional<Avatar> findByStudentId(Long studentId);

  Optional<Avatar> deleteByStudentId(Long studentId);

  boolean existsByStudentId(Long studentId);

  @Query(value = "SELECT * FROM avatar ORDER BY id LIMIT 4 OFFSET ?", nativeQuery = true)
  Iterable<Avatar> findAllByPage04(int pageNumber);


}//
